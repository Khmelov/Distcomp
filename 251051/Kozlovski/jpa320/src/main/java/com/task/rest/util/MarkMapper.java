package com.task.rest.util;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarkMapper {
    Mark toEntity(MarkRequestTo requestTo);
    MarkResponseTo toResponse(Mark mark);
    void updateEntityFromDto(MarkRequestTo requestTo, @MappingTarget Mark mark);
}