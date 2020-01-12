package org.bredkowiak.mongorest.beacon;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeaconRepoCustomImpl implements BeaconRepoCustom {

    private final MongoTemplate mongoTemplate;

    public BeaconRepoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Beacon> findLocationsWithCriteria(Criteria criteria) {
        Query query = new Query(criteria);
        List<Beacon> result = mongoTemplate.find(query, Beacon.class);
        return result;
    }
}
