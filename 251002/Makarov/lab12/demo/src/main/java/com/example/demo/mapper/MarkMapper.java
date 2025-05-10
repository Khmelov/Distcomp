package com.example.demo.mapper;

import com.example.demo.dto.MarkRequestTo;
import com.example.demo.dto.MarkResponseTo;
import com.example.demo.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    Mark toEntity(MarkRequestTo dto);

    MarkResponseTo toDto(Mark entity);

    void updateEntityFromDto(MarkRequestTo request, @MappingTarget Mark entity);
}
