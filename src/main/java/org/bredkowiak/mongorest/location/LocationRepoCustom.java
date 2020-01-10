package org.bredkowiak.mongorest.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface LocationRepoCustom {

    List<Location> findLocationsWithCriteria(Criteria criteria);
    Page<Location> findPageableLocationsWithCriteria(Criteria criteria, Pageable pageable);

}
