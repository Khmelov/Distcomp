package com.example.forum.mapper;

import com.example.forum.dto.request.MarkRequestTo;
import com.example.forum.dto.response.MarkResponseTo;
import com.example.forum.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {

    @Mapping(target = "id", ignore = true)
    Mark toEntity(MarkRequestTo dto);

    MarkResponseTo toResponse(Mark entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MarkRequestTo dto, @MappingTarget Mark entity);
}
