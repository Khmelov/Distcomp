package com.adashkevich.rest.lab.mapper;

import com.adashkevich.rest.lab.dto.request.MarkerRequestTo;
import com.adashkevich.rest.lab.dto.response.MarkerResponseTo;
import com.adashkevich.rest.lab.model.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);
    MarkerResponseTo toResponse(Marker entity);
}
