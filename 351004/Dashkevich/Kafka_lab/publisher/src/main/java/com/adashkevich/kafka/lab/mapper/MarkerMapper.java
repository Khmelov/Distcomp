package com.adashkevich.kafka.lab.mapper;

import com.adashkevich.kafka.lab.dto.request.MarkerRequestTo;
import com.adashkevich.kafka.lab.dto.response.MarkerResponseTo;
import com.adashkevich.kafka.lab.model.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);
    MarkerResponseTo toResponse(Marker entity);
}
