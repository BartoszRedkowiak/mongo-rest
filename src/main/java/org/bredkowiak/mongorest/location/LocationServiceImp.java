package org.bredkowiak.mongorest.location;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationServiceImp implements LocationService {

    private final String NOT_FOUND_MESSAGE = "No location with given id present in database";
    private final LocationRepository locationRepository;

    public LocationServiceImp(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location findOne(String id) throws NotFoundException {
        Optional<Location> location = this.locationRepository.findById(id);
        if (!location.isPresent()){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        return location.get();
    }

    @Override
    public List<Location> findLocations(Criteria criteria) {
        List<Location> locations = locationRepository.findLocationsWithCriteria(criteria);
        return locations;
    }

    @Override
    public Page<Location> findLocationPage(Pageable pageable) throws NotFoundException {
        //FIXME add filtering (category, distance)
        //TODO add sorting of some kind
        Page<Location> locations = locationRepository.findAll(pageable);
        if (!locations.hasContent()){
            throw new NotFoundException("Cannot generate page with provided query criteria");
        }
        return locations;
    }

    @Override
    public Location create(Location location) {
        //FIXME validate object
        //FIXME handle save failure
        Location savedLocation = locationRepository.insert(location);
        return savedLocation;
    }

    @Override
    public void update(Location location) {
        //FIXME validate object
        //FIXME handle update failure
        this.locationRepository.save(location);
    }

    @Override
    public void delete(String id) throws NotFoundException {
        boolean test = locationRepository.existsById(id);
        if (!test){
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        this.locationRepository.deleteById(id);
    }
}
