package com.task.rest.util;

import com.task.rest.dto.WriterRequestTo;
import com.task.rest.dto.WriterResponseTo;
import com.task.rest.model.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WriterMapper {
    Writer toEntity(WriterRequestTo requestTo);
    WriterResponseTo toResponse(Writer writer);
    void updateEntityFromDto(WriterRequestTo requestTo, @MappingTarget Writer writer);
}
