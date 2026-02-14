package com.example.restApi.mapper;

import com.example.restApi.dto.request.MarkerRequestTo;
import com.example.restApi.dto.response.MarkerResponseTo;
import com.example.restApi.model.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo request);
    MarkerResponseTo toDto(Marker marker);
    void updateEntity(@MappingTarget Marker marker, MarkerRequestTo request);
}
