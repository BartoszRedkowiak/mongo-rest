package org.bredkowiak.mongorest.beacon;

import com.mongodb.MongoWriteException;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.scheduler.EventSchedulerService;
import org.modelmapper.ModelMapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BeaconServiceImp implements BeaconService {

    private final String NOT_FOUND_MESSAGE = "No beacon with given id present in database";
    private final BeaconRepository beaconRepository;
    private final EventSchedulerService schedulerService;
    private final ModelMapper modelMapper;

    @Autowired
    public BeaconServiceImp(BeaconRepository beaconRepository, EventSchedulerService schedulerService, ModelMapper modelMapper) {
        this.beaconRepository = beaconRepository;
        this.schedulerService = schedulerService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Beacon findOne(String id) throws NotFoundException {
        Optional<Beacon> beacon = this.beaconRepository.findById(id);
        if (!beacon.isPresent()){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        return beacon.get();
    }

    @Override
    public List<Beacon> findLocations(Criteria criteria) {
        List<Beacon> beacons = beaconRepository.findLocationsWithCriteria(criteria);
        return beacons;
    }

    @Override
    public Page<Beacon> findLocationPage(Pageable pageable) throws NotFoundException {
        //FIXME add filtering (category, distance)
        //TODO add sorting of some kind
        Page<Beacon> beacons = beaconRepository.findAll(pageable);
        if (!beacons.hasContent()){
            throw new NotFoundException("Cannot generate page with provided query criteria");
        }
        return beacons;
    }

    @Override
    public Beacon create(Beacon beacon) throws MongoWriteException, SchedulerException {
        try {
            beacon = schedulerService.createNewEventCycle(beacon);
        } catch (NotFoundException e) {
            //FIXME find a way to inform client about not activating any event with the beacon
        }
        Beacon savedBeacon = beaconRepository.insert(beacon);
        return savedBeacon;
    }

    @Override
    public Beacon update(Beacon beacon) throws SchedulerException, NotFoundException {
        schedulerService.disableEventCycle(beacon.getJobName()); //FIXME
        Beacon beaconWithJobName = schedulerService.createNewEventCycle(beacon);
        Beacon updatedBeacon = beaconRepository.save(beaconWithJobName);
        return updatedBeacon;
    }

    @Override
    public void delete(String id) throws NotFoundException, SchedulerException {
        Optional<Beacon> beacon = beaconRepository.findById(id);
        if (!beacon.isPresent()){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        schedulerService.disableEventCycle(beacon.get().getJobName());
        this.beaconRepository.deleteById(id);
    }


}
