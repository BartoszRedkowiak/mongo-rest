package org.bredkowiak.mongorest.beacon;

import com.mongodb.MongoWriteException;
import io.swagger.annotations.ApiOperation;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.utils.ApiCallResponse;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;
import org.hibernate.validator.constraints.Range;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/beacons")
@Validated
public class BeaconController {

    private final BeaconService beaconService;

    @Autowired
    public BeaconController(BeaconService beaconService) {
        this.beaconService = beaconService;
    }

    @GetMapping("/{beaconId}")
    @ApiOperation(value = "Provides one beacon objects with given id", response = BeaconDTO.class)
    public ResponseEntity getOneBeacon(@PathVariable("beaconId") String id) {
        try{
            BeaconDTO beaconDTO = beaconService.findOne(id);
            return ResponseEntity.ok().body(beaconDTO);
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/map")
    @ApiOperation(value = "Provides list of beacon objects in area specified by query parameters", response = BeaconDTO.class)
    public ResponseEntity getLocations(@RequestParam(name = "radius") @Min(1) Integer radius,
                                       @RequestParam(name = "lat") @Range(min = -85, max = 85) Double lat,
                                       @RequestParam(name = "lng") @Range(min = -180, max = 180) Double lng ) {

        Criteria criteria = new Criteria();
        Double radiusConverted = 360.0 / 40075 * Double.valueOf(radius); // approx. kilometers to degree conversion
        criteria.where("latitude").lt(lat + radiusConverted).gt(lat - radiusConverted)
                .and("longitude").lt(lng + radiusConverted).gt(lng - radiusConverted);

        List<BeaconDTO> beaconDTOs = beaconService.findLocations(criteria);
        return ResponseEntity.status(HttpStatus.OK).body(beaconDTOs);
    }

    @GetMapping("/list")
    @ApiOperation(value = "Provides a page of beacon objects specified by query parameters", response = BeaconDTO.class)
    public ResponseEntity getLocationsPage(@RequestParam("page") Integer page,
                                           @RequestParam("size") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            List<BeaconDTO> beaconDTOs = beaconService.findLocationPage(pageable);
            return ResponseEntity.status(HttpStatus.OK).body(beaconDTOs);
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @ApiOperation(value = "Saves new beacon object in database and schedules events cycle in beacon area with specified time interval (in days)",
            notes = "Rejects objects that contains id", response = BeaconDTO.class)
    public ResponseEntity addBeacon(@RequestBody @Validated({ValidationCreate.class}) BeaconDTO beaconDTO) {
        try {
            beaconDTO = beaconService.create(beaconDTO);
        } catch (MongoWriteException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(beaconDTO);
    }

    @PutMapping
    @ApiOperation(value = "Updates a beacon object with given id", notes = "Rejects objects with missing or invalid id")
    public ResponseEntity updateBeacon(@RequestBody @Validated({ValidationUpdate.class}) BeaconDTO beaconDTO) {
        try {
            beaconService.update(beaconDTO);
        } catch (SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiCallResponse(false, e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/{beaconId}")
    @ApiOperation(value = "Deletes a beacon object with given id and scheduled event associated with the beacon area")
    public ResponseEntity deleteBeacon(@PathVariable("beaconId") String id) {
        try {
            beaconService.delete(id);
        } catch (NotFoundException | SchedulerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }





}
