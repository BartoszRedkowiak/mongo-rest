package org.bredkowiak.mongorest.location;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {

    Location findOne(String id) throws NotFoundException;
    List<Location> findLocations(); //TODO add optional parameters
    Page<Location> findLocationPage(Pageable pageable) throws NotFoundException; //TODO add optional parameters
    Location create (Location location);
    void update (Location location);
    void delete (String id) throws NotFoundException;

}
