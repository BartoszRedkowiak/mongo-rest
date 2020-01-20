package org.bredkowiak.mongorest.location;

import com.mongodb.MongoWriteException;
import io.swagger.annotations.ApiOperation;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.SubCategory;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.utils.ApiCallResponse;
import org.bredkowiak.mongorest.validation.ValidationCreate;
import org.bredkowiak.mongorest.validation.ValidationUpdate;
import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
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
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Validated
public class LocationController {

    //TODO getRecentLocations

    //TODO getEventLocations

    //TODO fully dynamic queries with pagination using querydsl

    private final LocationService locationService;
    private final ModelMapper modelMapper;

    @Autowired
    public LocationController(LocationService locationService, ModelMapper modelMapper) {
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    private LocationDTO toDTO(Location location){
        return modelMapper.map(location, LocationDTO.class);
    }

    private Location fromDTO(LocationDTO locationDTO){
        return modelMapper.map(locationDTO, Location.class);
    }

    @GetMapping("/{locationId}")
    @ApiOperation(value = "Provides one location objects with given id", response = Location.class )
    public ResponseEntity getOneLocation(@PathVariable("locationId") String id) {
        try{
            Location location = locationService.findOne(id);
            return ResponseEntity.ok().body(location);
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }

    }

    @GetMapping("/map")
    @ApiOperation(value = "Provides list of location objects in area specified by query parameters", notes = "Provide either category or subcategory, both parameters are redundant", response = Location.class)
    public ResponseEntity getLocations(@RequestParam(name = "radius") @Min(1) Integer radius,
                                       @RequestParam(name = "lat") @Range(min = -85, max = 85) Double lat,
                                       @RequestParam(name = "lng") @Range(min = -85, max = 85) Double lng,
                                       @RequestParam(name = "category", required = false) MainCategory catMain,
                                       @RequestParam(name = "subcategory", required = false) EnumSet<SubCategory> catSub ){

        //Add location criteria
        Criteria criteria = new Criteria();
        Double radiusConverted = 360.0 / 40075 * Double.valueOf(radius); // approx. kilometers to degree conversion
        criteria.where("latitude").lt(lat + radiusConverted).gt(lat - radiusConverted)
                .and("longitude").lt(lng + radiusConverted).gt(lng - radiusConverted);

        //Validate params and add category criteria
        if (catSub != null) {
            //TODO validate
            criteria.and("categories.subCategories").in(catSub);
        } else {
            if (catMain != null) {
                //TODO validate
                criteria.and("categories.mainCategory").is(catMain);
            }
        }

        List<Location> locations = locationService.findLocations(criteria);
        return ResponseEntity.status(200).body(locations);
    }

    @GetMapping("/list")
    @ApiOperation(value = "Provides a page of location objects specified by query parameters", response = Location.class)
    public ResponseEntity getLocationsPage(@RequestParam("page") @Min(0) Integer page,
                                           @RequestParam("size") @Min(1) Integer size ) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<Location> resultPage = locationService.findLocationPage(pageable);
            return ResponseEntity.status(200).body(resultPage.getContent());
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    @ApiOperation(value = "Saves new location object in database, returns created object with id", notes = "Rejects objects that contains id", response = LocationDTO.class)
    public ResponseEntity addLocation(@RequestBody @Validated({ValidationCreate.class}) LocationDTO locationDTO) {
        Location location = fromDTO(locationDTO);
        try {
            Location savedLocation = locationService.create(location); //TODO check how to catch creation failure
            return ResponseEntity.status(HttpStatus.OK)
                    .body(toDTO(savedLocation));
        } catch (MongoWriteException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
    }

    @PutMapping
    @ApiOperation(value = "Updates a location object with given", notes = "Rejects objects with missing or invalid id", response = LocationDTO.class)
    public ResponseEntity updateLocation(@RequestBody @Validated({ValidationUpdate.class}) LocationDTO locationDTO) {
        Location location = fromDTO(locationDTO);
        Location updatedLocation = locationService.update(location); //FIXME end-point currently allows changing activeEvent property
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(updatedLocation));
    }

    @DeleteMapping("/{locationId}")
    @ApiOperation(value = "Deletes a location object with given id")
    public ResponseEntity deleteLocation(@PathVariable("locationId") String id) throws NotFoundException {
        try {
            locationService.delete(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiCallResponse(true, "Location deleted successfully"));
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
    }
}
