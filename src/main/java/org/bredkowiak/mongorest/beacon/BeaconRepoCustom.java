package org.bredkowiak.mongorest.beacon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface BeaconRepoCustom {

    List<Beacon> findLocationsWithCriteria(Criteria criteria);

}
