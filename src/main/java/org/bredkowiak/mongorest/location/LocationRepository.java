package org.bredkowiak.mongorest.location;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends MongoRepository<Location, String>, LocationRepoCustom, QuerydslPredicateExecutor<Location> {

}
