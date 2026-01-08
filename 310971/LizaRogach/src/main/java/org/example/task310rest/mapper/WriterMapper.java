package org.example.task310rest.mapper;

import org.example.task310rest.dto.WriterRequestTo;
import org.example.task310rest.dto.WriterResponseTo;
import org.example.task310rest.model.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    Writer toEntity(WriterRequestTo request);

    WriterResponseTo toDto(Writer entity);

    void updateEntityFromDto(WriterRequestTo request, @MappingTarget Writer entity);
}


