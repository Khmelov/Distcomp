package by.bsuir.dc.lab3.services.impl;

import by.bsuir.dc.lab3.entities.Marker;
import by.bsuir.dc.lab3.services.interfaces.MarkerService;
import by.bsuir.dc.lab3.services.repos.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarkerServiceImpl implements MarkerService {

    @Autowired
    private MarkerRepository repos;
    @Override
    public Marker add(Marker marker) {
        return repos.save(marker);
    }

    @Override
    public void delete(long id) {
        Optional<Marker> marker = repos.findById(id);
        if(marker.isPresent()) {
            repos.delete(marker.get());
        }
    }

    @Override
    public Marker update(Marker marker) {
        return repos.saveAndFlush(marker);
    }

    @Override
    public Marker getById(long id) {
        Optional<Marker> marker = repos.findById(id);
        if(marker.isPresent()) {
            return marker.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Marker> getAll() {
        return repos.findAll();
    }

    @Override
    public Marker getByName(String name) {
        return repos.findByName(name);
    }
}
