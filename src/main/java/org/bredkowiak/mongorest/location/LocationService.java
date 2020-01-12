package org.bredkowiak.mongorest.location;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface LocationService {

    Location findOne(String id) throws NotFoundException;
    List<Location> findLocations(Criteria criteria);
    Page<Location> findLocationPage(Pageable pageable) throws NotFoundException;
    Location create (Location location);
    void update (Location location);
    void delete (String id) throws NotFoundException;

}
