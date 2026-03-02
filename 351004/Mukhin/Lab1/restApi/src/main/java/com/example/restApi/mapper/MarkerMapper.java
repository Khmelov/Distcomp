package com.example.restApi.mapper;

import com.example.restApi.dto.request.MarkerRequestTo;
import com.example.restApi.dto.response.MarkerResponseTo;
import com.example.restApi.model.Marker;
import org.springframework.stereotype.Component;

@Component
public class MarkerMapper {

    public Marker toEntity(MarkerRequestTo request) {
        Marker marker = new Marker();
        marker.setName(request.getName());
        return marker;
    }

    public MarkerResponseTo toDto(Marker marker) {
        MarkerResponseTo response = new MarkerResponseTo();
        response.setId(marker.getId());
        response.setName(marker.getName());
        return response;
    }

    public void updateEntity(Marker marker, MarkerRequestTo request) {
        marker.setName(request.getName());
    }
}
