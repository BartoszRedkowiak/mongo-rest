package org.bredkowiak.mongorest.beacon;

import com.mongodb.MongoWriteException;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface BeaconService {

    BeaconDTO findOne(String id) throws NotFoundException;
    List<BeaconDTO> findLocations(Criteria criteria);
    List<BeaconDTO> findLocationPage(Pageable pageable) throws NotFoundException;
    BeaconDTO create(BeaconDTO beaconDTO) throws MongoWriteException, SchedulerException;
    void update(BeaconDTO beaconDTO) throws SchedulerException, NotFoundException;
    void delete(String id) throws NotFoundException, SchedulerException;


}
