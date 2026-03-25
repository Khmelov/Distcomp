package com.lizaveta.notebook.mapper;

import com.lizaveta.notebook.model.dto.request.WriterRequestTo;
import com.lizaveta.notebook.model.dto.response.WriterResponseTo;
import com.lizaveta.notebook.model.entity.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WriterMapper {

    @Mapping(target = "id", ignore = true)
    Writer toEntity(WriterRequestTo request);

    WriterResponseTo toResponse(Writer entity);
}
