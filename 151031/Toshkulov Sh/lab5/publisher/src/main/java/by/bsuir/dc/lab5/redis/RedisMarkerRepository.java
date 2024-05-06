package by.bsuir.dc.lab5.redis;

import by.bsuir.dc.lab5.entities.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisMarkerRepository {

    @Autowired
    private RedisTemplate template;
    private static final String HASH_KEY = "Marker";


    public Marker add(Marker marker){
        try {
            template.opsForHash().put(HASH_KEY, marker.getId(), marker);
            template.expire(HASH_KEY,10, TimeUnit.SECONDS);
            return marker;
        }catch (Exception e){
            return null;
        }
    }

    public Marker update(Marker marker){
        Optional<Marker> currentMarker = getById(marker.getId());
        if(currentMarker.isPresent()){
            template.opsForHash().put(HASH_KEY, marker.getId(), marker);
            template.expire(HASH_KEY,10, TimeUnit.SECONDS);
            return marker;
        }else{
            return null;
        }
    }

    public void delete(long id){
        template.opsForHash().delete(HASH_KEY,id);
    }

    public List<Marker> getAll(){
        List<Marker> marker = new ArrayList<>();
        List<Object> data = template.opsForHash().values(HASH_KEY);
        for(Object obj : data){
            marker.add((Marker) obj);
        }
        return marker;
    }

    public Optional<Marker> getById(long id){
        Object marker = template.opsForHash().get(HASH_KEY,id);
        return Optional.ofNullable((Marker)marker);
    }
}
