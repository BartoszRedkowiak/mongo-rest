package org.bredkowiak.mongorest.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationRepoCustomImpl implements LocationRepoCustom {

    private final MongoTemplate mongoTemplate;

    public LocationRepoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Location> findLocationsWithCriteria(Criteria criteria) {
        Query query = new Query(criteria);

        List<Location> result = mongoTemplate.find(query, Location.class);
        return result;
    }

    @Override
    public Page<Location> findPageableLocationsWithCriteria(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria);
        return null;
    }
}
