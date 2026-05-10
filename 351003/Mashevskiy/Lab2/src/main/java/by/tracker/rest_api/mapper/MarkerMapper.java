package by.tracker.rest_api.mapper;

import by.tracker.rest_api.dto.MarkerRequestDto;
import by.tracker.rest_api.dto.MarkerResponseDto;
import by.tracker.rest_api.entity.Marker;
import org.springframework.stereotype.Component;

@Component
public class MarkerMapper {

    public Marker toEntity(MarkerRequestDto dto) {
        if (dto == null) return null;
        Marker marker = new Marker();
        marker.setId(dto.getId());
        marker.setName(dto.getName());
        return marker;
    }

    public MarkerResponseDto toResponseDto(Marker entity) {
        if (entity == null) return null;
        return new MarkerResponseDto(entity.getId(), entity.getName());
    }

    public void updateEntity(MarkerRequestDto dto, Marker entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
    }
}