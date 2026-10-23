package com.distcomp.mapper;

import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.dto.response.TagResponseTo;
import com.distcomp.model.Tag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Converts {@link Tag} entities to and from their transport objects.
 */
@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagRequestTo request);

    TagResponseTo toResponse(Tag tag);

    List<TagResponseTo> toResponseList(List<Tag> tags);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(TagRequestTo request, @MappingTarget Tag tag);
}
