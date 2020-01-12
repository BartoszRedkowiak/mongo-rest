package org.bredkowiak.mongorest.beacon;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.location.Location;
import org.bredkowiak.mongorest.location.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BeaconServiceImpl implements BeaconService {

    private final String NOT_FOUND_MESSAGE = "No beacon with given id present in database";
    private final BeaconRepository beaconRepository;

    public BeaconServiceImpl(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
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
    public Beacon create(Beacon beacon) {
        //FIXME validate object
        //FIXME handle save failure
        Beacon savedBeacon = beaconRepository.insert(beacon);
        return savedBeacon;
    }

    @Override
    public void update(Beacon beacon) {
        this.beaconRepository.save(beacon);
    }

    @Override
    public void delete(String id) throws NotFoundException {
        boolean test = beaconRepository.existsById(id);
        if (!test){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        this.beaconRepository.deleteById(id);
    }


}
