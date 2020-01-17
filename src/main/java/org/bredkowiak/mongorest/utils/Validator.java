package org.bredkowiak.mongorest.utils;

import org.bredkowiak.mongorest.category.Category;
import org.bredkowiak.mongorest.category.MainCategory;
import org.bredkowiak.mongorest.category.SubCategory;
import org.bredkowiak.mongorest.location.Location;

import java.util.Collections;
import java.util.EnumSet;

public class Validator {

    private static final Double MAX_LAT = 85d;
    private static final Double MIN_LAT = -85d;
    private static final Double MAX_LNG = 180d;
    private static final Double MIN_LNG = -180d;
    private static final int MIN_NAME_LENGTH = 5;
    private static final int ID_LENGTH = 24;
    private static final EnumSet<SubCategory> SUBCAT_LONGBOARD =
            EnumSet.of(SubCategory.DANCING, SubCategory.CRUISING, SubCategory.FREESTYLE, SubCategory.DOWNHILL);


    public static ValidationResult validateNewLocation(Location location){
        ValidationResult result = new ValidationResult();
        if (location.getId() != null) {
            result.addCause("Object cannot contain id parameter when creating new database entry");
        }
        result = testLocationAttributes(location, result);
        result = testCategoryAttributes(location.getCategories(), result);
        return result;
    }

    public static ValidationResult validateUpdatedLocation(Location location) {
        ValidationResult result = new ValidationResult();
        if (location.getId().trim().length() != ID_LENGTH) {
            result.addCause("Invalid length of id parameter");
        }
        result = testLocationAttributes(location, result);
        result = testCategoryAttributes(location.getCategories(), result);
        return result;
    }

    public static ValidationResult validateQueryParams(Integer radius, Double lat, Double lng) {
        ValidationResult result = new ValidationResult();
        if (radius <= 0) {
            result.addCause("Radius parameter must be greater than 0");
        }
        if (lat <= -85 || lat >= 85) {
            result.addCause("Latitude param cannot be outside -85 to 85 degree range");
        }
        if (lng <= -180 || lng >= 180) {
            result.addCause("Longitude param cannot be outside -180 to 180 degree range");
        }
        return result;
    }

    public static ValidationResult paginationTest(Integer page, Integer size) {
        ValidationResult result = new ValidationResult();
        if (page == null || size == null){
            result.addCause("Missing page parameter(s)");
            return result;
        }
        if (page < 0 || size <= 0){
            result.addCause("Improper pagination parameters");
        }
        return result;
    }

    private static ValidationResult testLocationAttributes(Location location, ValidationResult result) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        String name = location.getName().trim();

        if (lat < MIN_LAT || lat > MAX_LAT) {
            result.addCause("Latitude param outside of" + MIN_LAT + " to " + MAX_LAT + " range");
        }
        if (lng < MIN_LNG || lng > MAX_LNG) {
            result.addCause("Longitude param outside of" + MIN_LNG + " to " + MAX_LNG + " range");
        }
        if (name == null || name.length() <= MIN_NAME_LENGTH) {
            result.addCause("Location name must have at least " + MIN_NAME_LENGTH + " characters");
        }
        return result;
    }

    private static ValidationResult testCategoryAttributes(Category category, ValidationResult result) {
        EnumSet<SubCategory> subCategories = category.getSubCategories();
        MainCategory mainCategory = category.getMainCategory();

        if (mainCategory == null || subCategories == null || subCategories.isEmpty()) {
            result.addCause("Categories params cannot be empty");
        }

        boolean test = Collections.disjoint(subCategories, SUBCAT_LONGBOARD);
        if (mainCategory.equals(MainCategory.SKATEBOARD) && !test) {
            result.addCause("One of subcategories elements doesn't belong to main category");
        }
        if (mainCategory.equals(MainCategory.LONGBOARD) && test) {
            result.addCause("One of subcategories elements doesn't belong to main category");
        }
        return result;
    }

}
