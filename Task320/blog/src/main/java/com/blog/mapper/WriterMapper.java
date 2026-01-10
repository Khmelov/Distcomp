package com.blog.mapper;

import com.blog.dto.WriterRequestTo;
import com.blog.dto.WriterResponseTo;
import com.blog.entity.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WriterMapper {

    Writer requestToToEntity(WriterRequestTo request);

    WriterResponseTo entityToResponseTo(Writer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Writer updateEntityFromRequest(WriterRequestTo request, @MappingTarget Writer entity);
}