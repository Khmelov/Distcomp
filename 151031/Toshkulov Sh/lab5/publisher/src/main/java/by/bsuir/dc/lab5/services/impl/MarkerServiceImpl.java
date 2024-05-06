package by.bsuir.dc.lab5.services.impl;

import by.bsuir.dc.lab5.entities.Marker;
import by.bsuir.dc.lab5.redis.RedisMarkerRepository;
import by.bsuir.dc.lab5.services.interfaces.MarkerService;
import by.bsuir.dc.lab5.services.repos.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkerServiceImpl implements MarkerService {

    @Autowired
    private MarkerRepository repos;

    @Autowired
    private RedisMarkerRepository redisRepos;

    @Override
    public Marker add(Marker marker) {
        Marker saved = repos.save(marker);
        redisRepos.add(saved);
        return saved;
    }

    @Override
    public void delete(long id) {
        Optional<Marker> marker = repos.findById(id);
        if(marker.isPresent()) {
            redisRepos.delete(id);
            repos.delete(marker.get());
        }
    }

    @Override
    public Marker update(Marker marker) {
        Optional<Marker> cachedMarker = redisRepos.getById(marker.getId());
        if(cachedMarker.isPresent() && marker.equals(cachedMarker.get())){
            return marker;
        }
        Marker updatedMarker = repos.saveAndFlush(marker);
        redisRepos.update(updatedMarker);
        return updatedMarker;
    }

    @Override
    public Marker getById(long id) {
        Optional<Marker> marker = redisRepos.getById(id);

        if(marker.isPresent()) {
            return marker.get();
        } else {
            marker = repos.findById(id);

            if(marker.isPresent()) {
                redisRepos.add(marker.get());
                return marker.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Marker> getAll() {
        List<Marker> markers = redisRepos.getAll();
        if(!markers.isEmpty()){
            return markers;
        }else{
            markers = repos.findAll();
            if(!markers.isEmpty()){
                for(Marker currentMarkers : markers){
                    redisRepos.add(currentMarkers);
                }
                return markers;
            }else{
                return new ArrayList<>();
            }
        }
    }

    @Override
    public Marker getByName(String name) {
        return repos.findByName(name);
    }
}
