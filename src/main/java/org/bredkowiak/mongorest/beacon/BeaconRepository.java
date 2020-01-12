package org.bredkowiak.mongorest.beacon;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeaconRepository extends MongoRepository<Beacon, String>, BeaconRepoCustom {

}
