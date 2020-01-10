package org.bredkowiak.mongorest.location;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    /*
    - get all (for test)
    - get locations with filters:
        - distance (radius)
        - category
    - get page of locations
        - must consider filters
     */

    //TODO getRecentLocation

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<Location> getOneLocation(@PathVariable("locationId") String id) throws NotFoundException {
        Location location = locationService.findOne(id);
        return ResponseEntity.status(200).body(location);
    }

    @GetMapping
    public ResponseEntity getLocations(@RequestParam(name = "radius", required = false) Integer radius,
                                       @RequestParam(name = "lat", required = false) Double lat,
                                       @RequestParam(name = "lng", required = false) Double lng) throws IllegalArgumentException {
        //FIXME change optional parameters to obligatory
        validateParams(radius, lat, lng);
        Criteria criteria = queryCriteriaBuilder(radius, lat, lng);

        List<Location> locations = locationService.findLocations(criteria);
        return ResponseEntity.status(200).body(locations);
    }

    private void validateParams(Integer radius, Double lat, Double lng){
        if (radius == null & lat == null & lng == null){
            return;
        }
        if (radius != null & radius <= 0 ){
            throw new IllegalArgumentException("Radius parameter cannot be negative or equal to 0");
        }
        if (lat != null){
            if (lng == null){
                throw new IllegalArgumentException("Missing longitude parameter");
            }
            if (lat <= -85 || lat >= 85 ){
                throw new IllegalArgumentException("Latitude param cannot be outside -85 to 85 degree range");
            }
        }
        if (lng != null){
            if (lat == null){
                throw new IllegalArgumentException("Missing latitude parameter");
            }
            if (lng <= -180 || lng >= 180 ){
                throw new IllegalArgumentException("Longitude param cannot be outside -180 to 180 degree range");
            }
        }
    }

    private Criteria queryCriteriaBuilder(Integer radius, Double lat, Double lng){
        Double distanceConverted = 360.0 / 40075 * Double.valueOf(radius);
        Double minLat = lat - distanceConverted;
        Double maxLat = lat + distanceConverted;
        Double minLng = lng - distanceConverted;
        Double maxLng = lng + distanceConverted;

        Criteria c = new Criteria();
        c.where("latitude").lt(maxLat).gt(minLat)
                .and("longitude").lt(maxLng).gt(minLng);
        return c;
    }

    @GetMapping("/page/{page}/size/{size}")
    public ResponseEntity getLocationsPage(@PathVariable("page") int page,
                                           @PathVariable("size") int size) throws NotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> resultPage = locationService.findLocationPage(pageable);
        return ResponseEntity.status(200).body(resultPage.getContent());
    }

    @PostMapping
    public ResponseEntity addLocation(@RequestBody Location location){
        //TODO handle exceptions
        Location savedLocation = locationService.create(location);
        return ResponseEntity.status(200).body(savedLocation);
    }

    @PutMapping
    public ResponseEntity updateLocation(@RequestBody Location location){
        //TODO handle exceptions
        locationService.update(location);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity deleteLocation(@PathVariable("locationId") String id) throws NotFoundException{
        //TODO handle exceptions
        locationService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
