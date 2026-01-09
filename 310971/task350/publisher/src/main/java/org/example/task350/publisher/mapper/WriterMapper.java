package org.example.task350.publisher.mapper;

import org.example.task350.publisher.dto.WriterRequestTo;
import org.example.task350.publisher.dto.WriterResponseTo;
import org.example.task350.publisher.model.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "tweets", ignore = true)
    Writer toEntity(WriterRequestTo request);

    WriterResponseTo toDto(Writer entity);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "tweets", ignore = true)
    void updateEntityFromDto(WriterRequestTo request, @MappingTarget Writer entity);
}

