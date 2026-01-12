package com.task.rest.mapper;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MarkMapper {

    MarkResponseTo toResponseTo(Mark mark);

    Mark toEntity(MarkRequestTo requestTo);
}