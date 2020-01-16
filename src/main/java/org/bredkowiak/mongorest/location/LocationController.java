package org.bredkowiak.mongorest.location;

import com.mongodb.MongoWriteException;
import io.swagger.annotations.ApiOperation;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.SubCategory;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.utils.ApiCallResponse;
import org.bredkowiak.mongorest.utils.ValidationResult;
import org.bredkowiak.mongorest.utils.Validator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    //TODO getRecentLocations

    //TODO getEventLocations

    //TODO fully dynamic queries with pagination using querydsl

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
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
    public ResponseEntity getLocations(@RequestParam(name = "radius") Integer radius,
                                       @RequestParam(name = "lat") Double lat,
                                       @RequestParam(name = "lng") Double lng,
                                       @RequestParam(name = "category", required = false) MainCategory catMain,
                                       @RequestParam(name = "subcategory", required = false) EnumSet<SubCategory> catSub ){
        Criteria criteria = new Criteria();

        //Validate required params
        ValidationResult validationResult = Validator.validateQueryParams(radius, lat, lng);
        if (!validationResult.isPassed()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiCallResponse(validationResult));
        }

        //Add location criteria
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
    public ResponseEntity getLocationsPage(@RequestParam("page") Integer page,
                                           @RequestParam("size") Integer size ) {
        ValidationResult validationResult = Validator.paginationTest(page, size);
        if (!validationResult.isPassed()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiCallResponse(validationResult));
        }

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
    @ApiOperation(value = "Saves new location object in database, returns created object with id", notes = "Rejects objects that contains id", response = ApiCallResponse.class)
    public ResponseEntity addLocation(@RequestBody Location location) {
        ValidationResult validationResult = Validator.validateNewLocation(location);
        if (!validationResult.isPassed()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiCallResponse(validationResult));
        }
        try {
            Location savedLocation = locationService.create(location); //TODO check how to catch creation failue
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiCallResponse(true, savedLocation.getId(), "Location created successfully"));
        } catch (MongoWriteException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiCallResponse(false, e.getMessage()));
        }
    }

    @PutMapping
    @ApiOperation(value = "Updates a location object with given", notes = "Rejects objects with missing or invalid id")
    public ResponseEntity updateLocation(@RequestBody Location location) {
        ValidationResult validationResult = Validator.validateUpdatedLocation(location);
        if (!validationResult.isPassed()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiCallResponse(validationResult));
        }
        locationService.update(location);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiCallResponse(true, "Location updated successfully"));
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
