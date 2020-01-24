package org.bredkowiak.mongorest.beacon;

import com.mongodb.MongoWriteException;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface BeaconService {

    Beacon findOne(String id) throws NotFoundException;
    Beacon create(Beacon beacon) throws MongoWriteException, SchedulerException;
    Beacon update(Beacon beacon) throws SchedulerException, NotFoundException;
    void delete(String id) throws NotFoundException, SchedulerException;
    List<Beacon> findLocations(Criteria criteria);
    Page<Beacon> findLocationPage(Pageable pageable) throws NotFoundException;
}
