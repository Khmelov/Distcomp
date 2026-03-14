package com.example.Labs.mapper;

import com.example.Labs.dto.request.MarkRequestTo;
import com.example.Labs.dto.response.MarkResponseTo;
import com.example.Labs.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    @Mapping(target = "id", ignore = true)
    Mark toEntity(MarkRequestTo dto);

    MarkResponseTo toDto(Mark entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(MarkRequestTo dto, @MappingTarget Mark entity);
}