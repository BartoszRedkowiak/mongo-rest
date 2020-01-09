package org.bredkowiak.mongorest.location;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity getLocations(){
        List<Location> locations = locationService.findLocations();
        return ResponseEntity.status(200).body(locations);
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
