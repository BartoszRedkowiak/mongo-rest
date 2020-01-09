package org.bredkowiak.mongorest.location;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImp implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImp(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location findOne(String id) throws NotFoundException {
        Optional<Location> location = this.locationRepository.findById(id);
        if (!location.isPresent()){
            throw new NotFoundException("No Location with given id present in database");
        }
        return location.get();
    }

    @Override
    public List<Location> findLocations() {
        //FIXME add filtering (category, distance)
        List<Location> locations = locationRepository.findAll();
        return locations;
    }

    @Override
    public Page<Location> findLocationPage(Pageable pageable) throws NotFoundException {
        //FIXME add filtering (category, distance)
        //TODO add sorting of some kind
        Page<Location> locations = locationRepository.findAll(pageable);
        if (!locations.hasContent()){
            throw new NotFoundException("Cannot generate more pages");
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
        Optional<Location> location = this.locationRepository.findById(id);
        if (!location.isPresent()){
            throw new NotFoundException("No Location with given id present in database");
        }
        this.locationRepository.deleteById(id);
    }
}
