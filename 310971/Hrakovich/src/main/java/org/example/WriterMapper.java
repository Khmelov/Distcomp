package org.example;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    Writer toEntity(WriterRequestTo dto);

    WriterResponseTo toResponse(Writer entity);
}
