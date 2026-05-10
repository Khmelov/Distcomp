package com.adashkevich.jpa.lab.mapper;

import com.adashkevich.jpa.lab.dto.request.MarkerRequestTo;
import com.adashkevich.jpa.lab.dto.response.MarkerResponseTo;
import com.adashkevich.jpa.lab.model.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);
    MarkerResponseTo toResponse(Marker entity);
}
