package org.bredkowiak.mongorest.beacon;

import io.swagger.annotations.ApiOperation;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.SubCategory;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.exception.ObjectValidationException;
import org.bredkowiak.mongorest.location.Location;
import org.bredkowiak.mongorest.utils.Validator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/beacons")
public class BeaconController {

    private final BeaconService beaconService;

    public BeaconController(BeaconService beaconService) {
        this.beaconService = beaconService;
    }

    @GetMapping("/{beaconId}")
    @ApiOperation(value = "Provides one beacon objects with given id", response = Beacon.class)
    public ResponseEntity<Beacon> getOneBeacon(@PathVariable("beaconId") String id) throws NotFoundException {
        Beacon beacon = beaconService.findOne(id);
        return ResponseEntity.status(200).body(beacon);
    }

    @GetMapping("/map")
    @ApiOperation(value = "Provides list of beacon objects in area specified by query parameters", response = Beacon.class)
    public ResponseEntity getLocations(@RequestParam(name = "radius") Integer radius,
                                       @RequestParam(name = "lat") Double lat,
                                       @RequestParam(name = "lng") Double lng
    ) throws IllegalArgumentException {
        Criteria criteria = new Criteria();

        //Validate params and add location criteria
        Validator.validateQueryParams(radius, lat, lng);
        Double radiusConverted = 360.0 / 40075 * Double.valueOf(radius); // approx. kilometers to degree conversion
        criteria.where("latitude").lt(lat + radiusConverted).gt(lat - radiusConverted)
                .and("longitude").lt(lng + radiusConverted).gt(lng - radiusConverted);

        List<Beacon> beacons = beaconService.findLocations(criteria);
        return ResponseEntity.status(200).body(beacons);
    }


    @GetMapping("/list")
    @ApiOperation(value = "Provides a page of beacon objects specified by query parameters", response = Beacon.class)
    public ResponseEntity getLocationsPage(@RequestParam("page") Integer page,
                                           @RequestParam("size") Integer size
    ) throws IllegalArgumentException, NotFoundException {
        //Validate params and prepare pageable object
        Validator.paginationTest(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Beacon> resultPage = beaconService.findLocationPage(pageable);
        return ResponseEntity.status(200).body(resultPage.getContent());
    }



    @PostMapping
    @ApiOperation(value = "Saves new beacon object in database, returns created object with id", notes = "Rejects objects that contains id", response = Beacon.class)
    public ResponseEntity addBeacon(@RequestBody Beacon beacon) throws ObjectValidationException {
//        Validator.validateNewBeacon(beacon);
        Beacon savedBeacon = beaconService.create(beacon);
        return ResponseEntity.status(200).body(savedBeacon);
    }

    @PutMapping
    @ApiOperation(value = "Updates a beacon object with given", notes = "Rejects objects with missing or invalid id")
    public ResponseEntity updateBeacon(@RequestBody Beacon beacon) throws ObjectValidationException {
//        Validator.validateUpdatedBeacon(beacon);
        beaconService.update(beacon);
        return ResponseEntity.status(200).build();
    }


    @DeleteMapping("/{beaconId}")
    @ApiOperation(value = "Deletes a beacon object with given id")
    public ResponseEntity deleteBeacon(@PathVariable("beaconId") String id) throws NotFoundException {
        beaconService.delete(id);
        return ResponseEntity.status(200).build();
    }





}
