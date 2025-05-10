package com.example.distcomp_1.repository;

import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.mdoel.Marker;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class MarkerRepository {
    private Long currentId = 0L;
    private final HashMap<Long, Marker> markers = new HashMap<>();

    public Marker add(Marker marker) {
        marker.setId(currentId++);
        markers.put(marker.getId(), marker);
        return marker;
    }
    public Marker get(long id) {
        return markers.get(id);
    }

    public List<Marker> getMarkers() {
        return new ArrayList<>(markers.values());
    }
    public void delete(long id) {
        markers.remove(id);
    }

    public Marker update(Marker marker) {
        markers.put(marker.getId(), marker);
        return marker;
    }
}
