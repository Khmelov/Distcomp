package by.bsuir.dc.lab1.inmemory;

import by.bsuir.dc.lab1.entities.Marker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MarkersTable {

    private static MarkersTable instance;
    private BigInteger id = BigInteger.valueOf(1);
    private List<Marker> markers = new ArrayList<>();

    private MarkersTable(){

    }

    public Marker add(Marker marker){
        marker.setId(id);
        markers.add(marker);
        id = id.add(BigInteger.valueOf(1));
        return marker;
    }
    public boolean delete(BigInteger id){
        for(Marker marker: markers){
            if(marker.getId().equals(id)){
                return markers.remove(marker);
            }
        }
        return false;
    }
    public Marker getById(BigInteger id){
        for(Marker t: markers){
            if(t.getId().equals(id)){
                return t;
            }
        }
        return null;
    }
    public List<Marker> getAll(){
        return markers;
    }

    public Marker update(Marker marker){
        Marker oldMarker = getById(marker.getId());
        if(oldMarker != null){
            markers.set(markers.indexOf(oldMarker), marker);
            return marker;
        } else {
            return null;
        }
    }

    public static MarkersTable getInstance(){
        if(instance == null){
            instance = new MarkersTable();
        }
        return instance;
    }
}
