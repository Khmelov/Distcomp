package com.labs.service.mapper;

import com.labs.domain.entity.Writer;
import com.labs.service.dto.WriterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WriterMapper {
    WriterDto toDto(Writer writer);
    Writer toEntity(WriterDto writerDto);
}

