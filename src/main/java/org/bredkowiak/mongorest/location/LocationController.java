package org.bredkowiak.mongorest.location;

import com.mongodb.DBObject;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.swagger.annotations.ApiOperation;
import org.bredkowiak.mongorest.category.Category;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.QCategory;
import org.bredkowiak.mongorest.category.SubCategory;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.exception.ObjectValidationException;
import org.bredkowiak.mongorest.utils.Validator;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
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

    //TODO getRecentLocations

    //TODO getEventLocations

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/{locationId}")
    @ApiOperation(value = "Provides one location objects with given id", response = Location.class)
    public ResponseEntity<Location> getOneLocation(@PathVariable("locationId") String id) throws NotFoundException {
        Location location = locationService.findOne(id);
        return ResponseEntity.status(200).body(location);
    }

    @GetMapping("/map")
    @ApiOperation(value = "Provides list of location objects in area specified by query parameters", notes = "Provide either category or subcategory, both parameters are redundant", response = Location.class)
    public ResponseEntity getLocations(@RequestParam(name = "radius") Integer radius,
                                       @RequestParam(name = "lat") Double lat,
                                       @RequestParam(name = "lng") Double lng,
                                       @RequestParam(name = "category", required = false) MainCategory catMain,
                                       @RequestParam(name = "subcategory", required = false) EnumSet<SubCategory> catSub
    ) throws IllegalArgumentException {
        Criteria criteria = new Criteria();

        //Validate params and add location criteria
        Validator.validateQueryParams(radius, lat, lng);
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

    //TODO fully dynamic queries with pagination using querydsl

    @GetMapping("/list")
    @ApiOperation(value = "Provides a page of location objects specified by query parameters", response = Location.class)
    public ResponseEntity getLocationsPage(@RequestParam("page") Integer page,
                                           @RequestParam("size") Integer size
                                           ) throws NotFoundException, IllegalArgumentException {
        //Validate params and prepare pageable object
        Validator.paginationTest(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> resultPage = locationService.findLocationPage(pageable);
        return ResponseEntity.status(200).body(resultPage.getContent());
    }

    @PostMapping
    @ApiOperation(value = "Saves new location object in database, returns created object with id", notes = "Rejects objects that contains id", response = Location.class)
    public ResponseEntity addLocation(@RequestBody Location location) throws ObjectValidationException {
        Validator.validateNewLocation(location);
        Location savedLocation = locationService.create(location);
        return ResponseEntity.status(200).body(savedLocation);
    }

    @PutMapping
    @ApiOperation(value = "Updates a location object with given", notes = "Rejects objects with missing or invalid id")
    public ResponseEntity updateLocation(@RequestBody Location location) throws ObjectValidationException {
        Validator.validateUpdatedLocation(location);
        locationService.update(location);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/{locationId}")
    @ApiOperation(value = "Deletes a location object with given id")
    public ResponseEntity deleteLocation(@PathVariable("locationId") String id) throws NotFoundException {
        locationService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
