package com.task.rest.mapper;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MarkMapper {
    Mark toEntity(MarkRequestTo dto);
    MarkResponseTo toDto(Mark entity);
    List<MarkResponseTo> toDtoList(List<Mark> entities);
}