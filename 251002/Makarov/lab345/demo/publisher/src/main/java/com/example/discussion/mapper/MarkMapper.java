package com.example.discussion.mapper;

import com.example.discussion.dto.MarkRequestTo;
import com.example.discussion.dto.MarkResponseTo;
import com.example.discussion.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarkMapper {
    Mark toEntity(MarkRequestTo dto);

    MarkResponseTo toDto(Mark entity);

    void updateEntityFromDto(MarkRequestTo request, @MappingTarget Mark entity);
}
