package com.restApp.mapper;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;
import com.restApp.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    Mark toEntity(MarkRequestTo request);

    MarkResponseTo toResponse(Mark entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    void updateEntity(@MappingTarget Mark entity, MarkRequestTo request);
}
