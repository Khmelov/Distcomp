package com.lizaveta.notebook.mapper;

import com.lizaveta.notebook.model.dto.request.MarkerRequestTo;
import com.lizaveta.notebook.model.dto.response.MarkerResponseTo;
import com.lizaveta.notebook.model.entity.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarkerMapper {

    @Mapping(target = "id", ignore = true)
    Marker toEntity(MarkerRequestTo request);

    MarkerResponseTo toResponse(Marker entity);
}
