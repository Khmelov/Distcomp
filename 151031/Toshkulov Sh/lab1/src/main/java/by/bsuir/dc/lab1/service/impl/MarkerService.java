package by.bsuir.dc.lab1.service.impl;

import by.bsuir.dc.lab1.dto.*;
import by.bsuir.dc.lab1.dto.mappers.MarkerMapper;
import by.bsuir.dc.lab1.entities.Marker;
import by.bsuir.dc.lab1.inmemory.MarkersTable;
import by.bsuir.dc.lab1.inmemory.NewsMarkerTable;
import by.bsuir.dc.lab1.service.abst.IMarkerService;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class MarkerService implements IMarkerService {

    @Override
    public MarkerResponseTo create(MarkerRequestTo markerTo) {
        Marker marker = MarkerMapper.instance.convertFromDTO(markerTo);
        marker = MarkersTable.getInstance().add(marker);
        NewsMarkerTable.getInstance().add(markerTo.getNewsId(),marker.getId());
        if(marker != null){
            return MarkerMapper.instance.convertToDTO(marker);
        } else {
            return null;
        }
    }

    @Override
    public MarkerResponseTo getById(BigInteger id) {
        Marker marker = MarkersTable.getInstance().getById(id);
        if(marker != null){
            return MarkerMapper.instance.convertToDTO(marker);
        } else {
            return null;
        }
    }

    @Override
    public List<MarkerResponseTo> getAll() {
        List<Marker> markers = MarkersTable.getInstance().getAll();
        List<MarkerResponseTo> markersTo = new ArrayList<>();
        for(Marker marker : markers){
            markersTo.add(MarkerMapper.instance.convertToDTO(marker));
        }
        return markersTo;
    }

    @Override
    public MarkerResponseTo update(MarkerRequestTo markerTo) {
        Marker updatedComment = MarkerMapper.instance.convertFromDTO(markerTo);
        Marker comment = MarkersTable.getInstance().update(updatedComment);
        if(comment != null){
            return MarkerMapper.instance.convertToDTO(comment);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(BigInteger id) {
        NewsMarkerTable.getInstance().deleteByMarkerId(id);
        return MarkersTable.getInstance().delete(id);
    }
}
