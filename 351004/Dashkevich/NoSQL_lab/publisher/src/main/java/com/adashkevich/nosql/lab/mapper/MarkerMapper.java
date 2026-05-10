package com.adashkevich.nosql.lab.mapper;

import com.adashkevich.nosql.lab.dto.request.MarkerRequestTo;
import com.adashkevich.nosql.lab.dto.response.MarkerResponseTo;
import com.adashkevich.nosql.lab.model.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker toEntity(MarkerRequestTo dto);
    MarkerResponseTo toResponse(Marker entity);
}
