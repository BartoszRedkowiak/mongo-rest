package org.bredkowiak.mongorest.validator;

import org.bredkowiak.mongorest.exception.ObjectValidationException;
import org.bredkowiak.mongorest.location.Location;

public class LocationValidator {

    private static final Double MAX_LAT = 85d;
    private static final Double MIN_LAT = -85d;
    private static final Double MAX_LNG = 180d;
    private static final Double MIN_LNG = -180d;
    private static final int MIN_NAME_LENGTH = 5;
    private static final int ID_LENGTH = 24;


    public static void validateNewLocation(Location location) throws ObjectValidationException{
        if (location.getId() != null){
            throw new ObjectValidationException("Object cannot contain id parameter when creating new database entry");
        }
        testLocationAttributes(location);
    }

    public static void validateUpdatedLocation(Location location) throws ObjectValidationException{
        if (location.getId().trim().length() != ID_LENGTH){
            throw new ObjectValidationException("Invalid length of id parameter");
        }
        testLocationAttributes(location);
    }

    public static void validateQueryParams(Integer radius, Double lat, Double lng) throws IllegalArgumentException {
        int exceptions = 0;
        StringBuilder message = new StringBuilder();
        message.append("Invalid params:");

        if (radius <= 0) {
            exceptions++;
            message.append(" (").append(exceptions).append(") ")
                    .append("Radius parameter must be greater than 0");
        }
        if (lat <= -85 || lat >= 85) {
            exceptions++;
            message.append(" (").append(exceptions).append(") ")
                    .append("Latitude param cannot be outside -85 to 85 degree range");
        }
        if (lng <= -180 || lng >= 180) {
            exceptions++;
            message.append(" (").append(exceptions).append(") ")
                    .append("Longitude param cannot be outside -180 to 180 degree range");
        }
        if (exceptions > 0){
            throw new IllegalArgumentException(message.toString());
        }
    }


    private static void testLocationAttributes(Location location) throws  ObjectValidationException{
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        String name = location.getName().trim();
        int exceptions = 0;
        StringBuilder message = new StringBuilder();
        message.append("Validation errors:");

        if (lat < MIN_LAT || lat > MAX_LAT){
            exceptions++;
            message.append(" (").append(exceptions).append(") ")
                    .append("Latitude param outside of" + MIN_LAT + " to " + MAX_LAT + " range");
        }
        if (lng < MIN_LNG || lng > MAX_LNG){
            exceptions++;
            message.append(" (").append(exceptions).append(") ")
                    .append("Longitude param outside of" + MIN_LNG + " to " + MAX_LNG + " range");
        }
        if (name == null || name.length() <= MIN_NAME_LENGTH){
            exceptions++;
            message.append(" (").append(exceptions).append(") ")
                    .append("Location name must have at least 5 characters");
        }
        if (exceptions > 0){
            throw new ObjectValidationException(message.toString());
        }
    }

}
