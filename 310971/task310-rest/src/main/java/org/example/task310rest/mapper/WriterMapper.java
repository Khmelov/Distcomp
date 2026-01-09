package org.example.task310rest.mapper;

import org.example.task310rest.dto.WriterRequestTo;
import org.example.task310rest.dto.WriterResponseTo;
import org.example.task310rest.model.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    @org.mapstruct.Mapping(target = "id", ignore = true)
    Writer toEntity(WriterRequestTo request);

    WriterResponseTo toDto(Writer entity);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    void updateEntityFromDto(WriterRequestTo request, @MappingTarget Writer entity);
}
