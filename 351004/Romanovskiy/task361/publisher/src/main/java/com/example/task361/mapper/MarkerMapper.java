package com.example.task361.mapper;

import com.example.task361.domain.dto.request.MarkerRequestTo;
import com.example.task361.domain.dto.response.MarkerResponseTo;
import com.example.task361.domain.entity.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo request);

    MarkerResponseTo toResponse(Marker marker);
}