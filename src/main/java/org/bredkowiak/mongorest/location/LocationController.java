package org.bredkowiak.mongorest.location;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.exception.ObjectValidationException;
import org.bredkowiak.mongorest.validator.LocationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity getLocations(@RequestParam(name = "radius") Integer radius,
                                       @RequestParam(name = "lat") Double lat,
                                       @RequestParam(name = "lng") Double lng) throws IllegalArgumentException {
        //TODO add filtering by category later
        LocationValidator.validateQueryParams(radius, lat, lng);
        Criteria criteria = queryCriteriaBuilder(radius, lat, lng);

        List<Location> locations = locationService.findLocations(criteria);
        return ResponseEntity.status(200).body(locations);
    }

    private Criteria queryCriteriaBuilder(Integer radius, Double lat, Double lng) {
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
    public ResponseEntity addLocation(@RequestBody Location location) throws ObjectValidationException {
        LocationValidator.validateNewLocation(location);
        Location savedLocation = locationService.create(location);
        return ResponseEntity.status(200).body(savedLocation);
    }

    @PutMapping
    public ResponseEntity updateLocation(@RequestBody Location location) throws ObjectValidationException {
        LocationValidator.validateUpdatedLocation(location);
        locationService.update(location);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity deleteLocation(@PathVariable("locationId") String id) throws NotFoundException {
        locationService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
