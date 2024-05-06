package by.bsuir.dc.lab1.inmemory;

import by.bsuir.dc.lab1.entities.Marker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsMarkerTable {

    private static NewsMarkerTable instance;

    private List<BigInteger> newsIds = new ArrayList<>();

    private List<BigInteger> markerIds = new ArrayList<>();

    private NewsMarkerTable(){

    }

    public void add(BigInteger newsId,BigInteger markerId){
        markerIds.add(markerId);
        newsIds.add(newsId);
    }
    public boolean deleteByMarkerId(BigInteger id){
        List<BigInteger> elementsToRemove = new ArrayList<>();
        for(BigInteger currentId : markerIds){
            if(currentId.equals(id)){
                elementsToRemove.add(currentId);
                newsIds.remove(markerIds.indexOf(currentId));
            }
        }
        markerIds.removeAll(elementsToRemove);
        return true;
    }
    public boolean deleteByNewsId(BigInteger id){
        List<BigInteger> elementsToRemove = new ArrayList<>();
        for(BigInteger currentId : newsIds){
            if(currentId.equals(id)){
                elementsToRemove.add(currentId);
                markerIds.remove(newsIds.indexOf(currentId));
            }
        }
        newsIds.removeAll(elementsToRemove);
        return true;
    }

    public static NewsMarkerTable getInstance(){
        if(instance == null){
            instance = new NewsMarkerTable();
        }
        return instance;
    }
}
