package org.rv.lab1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rv.lab1.domain.Marker;
import org.rv.lab1.dto.MarkerRequestTo;
import org.rv.lab1.dto.MarkerResponseTo;

@Mapper(componentModel = "spring")
public interface MarkerMapper {
    @Mapping(target = "id", ignore = true)
    Marker toEntity(MarkerRequestTo dto);

    void updateEntity(MarkerRequestTo dto, @MappingTarget Marker target);

    MarkerResponseTo toResponse(Marker marker);
}

