package com.blog.mapper;

import com.blog.dto.TagRequestTo;
import com.blog.dto.TagResponseTo;
import com.blog.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper {

    Tag requestToToEntity(TagRequestTo request);

    TagResponseTo entityToResponseTo(Tag entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Tag updateEntityFromRequest(TagRequestTo request, @MappingTarget Tag entity);
}