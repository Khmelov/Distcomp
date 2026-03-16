package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.MarkerRequestTo;
import by.tracker.rest_api.dto.MarkerResponseTo;
import by.tracker.rest_api.model.Marker;
import org.springframework.stereotype.Component;

@Component
public class MarkerMapper {

    public Marker toEntity(MarkerRequestTo request) {
        Marker marker = new Marker();
        marker.setId(request.getId());
        marker.setName(request.getName());
        return marker;
    }

    public MarkerResponseTo toResponse(Marker entity) {
        MarkerResponseTo response = new MarkerResponseTo();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
}