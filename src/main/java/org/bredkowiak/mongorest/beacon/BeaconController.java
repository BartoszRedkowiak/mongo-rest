package org.bredkowiak.mongorest.beacon;

import io.swagger.annotations.ApiOperation;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;
import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/beacons")
@Validated
public class BeaconController {

    private final BeaconService beaconService;
    private final ModelMapper modelMapper;

    @Autowired
    public BeaconController(BeaconServiceImp beaconService, ModelMapper modelMapper) {
        this.beaconService = beaconService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{beaconId}")
    @ApiOperation(value = "Provides one beacon objects with given id", response = BeaconDTO.class)
    public ResponseEntity<BeaconDTO> getOneBeacon(@PathVariable("beaconId") String id) throws NotFoundException {
        Beacon beacon = beaconService.findOne(id);
        return ResponseEntity.ok().body(toDTO(beacon));
    }

    @GetMapping("/map")
    @ApiOperation(value = "Provides list of beacon objects in area specified by query parameters", response = BeaconDTO.class)
    public ResponseEntity<List<BeaconDTO>> getBeacons(@RequestParam(name = "radius") @Min(1) Integer radius,
                                                        @RequestParam(name = "lat") @Range(min = -85, max = 85) Double lat,
                                                        @RequestParam(name = "lng") @Range(min = -180, max = 180) Double lng) {

        Criteria criteria = new Criteria();
        Double radiusConverted = 360.0 / 40075 * Double.valueOf(radius); // approx. kilometers to degree conversion
        criteria.where("latitude").lt(lat + radiusConverted).gt(lat - radiusConverted)
                .and("longitude").lt(lng + radiusConverted).gt(lng - radiusConverted);

        List<Beacon> beacons = beaconService.findLocations(criteria);
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(beacons));
    }

    @GetMapping("/list")
    @ApiOperation(value = "Provides a page of beacon objects specified by query parameters", response = BeaconDTO.class)
    public ResponseEntity<List<BeaconDTO>> getBeaconsPage(@RequestParam("page") Integer page,
                                                            @RequestParam("size") Integer size) throws NotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Beacon> beacons = beaconService.findLocationPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(beacons.getContent()));
    }

    @PostMapping
    @ApiOperation(value = "Saves new beacon object in database and schedules events cycle in beacon area with specified time interval (in days)",
            notes = "Rejects objects that contains id", response = BeaconDTO.class)
    public ResponseEntity<BeaconDTO> addBeacon(@RequestBody @Validated({ValidationCreate.class}) BeaconDTO beaconDTO)
            throws SchedulerException {
        Beacon savedBeacon = beaconService.create(fromDTO(beaconDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedBeacon));
    }

    @PutMapping
    @ApiOperation(value = "Updates a beacon object with given id", notes = "Rejects objects with missing or invalid id")
    public ResponseEntity<BeaconDTO> updateBeacon(@RequestBody @Validated({ValidationUpdate.class}) BeaconDTO beaconDTO) throws NotFoundException, SchedulerException {
        Beacon updatedBeacon = beaconService.update(fromDTO(beaconDTO));
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(updatedBeacon));
    }


    @DeleteMapping("/{beaconId}")
    @ApiOperation(value = "Deletes a beacon object with given id and scheduled event associated with the beacon area")
    public ResponseEntity<?> deleteBeacon(@PathVariable("beaconId") String id) throws NotFoundException, SchedulerException {
        beaconService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private BeaconDTO toDTO(Beacon beacon) {
        return modelMapper.map(beacon, BeaconDTO.class);
    }

    private List<BeaconDTO> toDTO(List<Beacon> beacons) {
        return beacons.stream().map( e -> modelMapper.map(e, BeaconDTO.class)).collect(Collectors.toList());
    }

    private Beacon fromDTO(BeaconDTO beaconDTO) {
        return modelMapper.map(beaconDTO, Beacon.class);
    }


}
