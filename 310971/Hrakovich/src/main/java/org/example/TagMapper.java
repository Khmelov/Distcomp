package org.example;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    Tag toEntity(TagRequestTo dto);

    TagResponseTo toResponse(Tag entity);

    TagShortResponseTo toShortResponse(Tag entity);

    default TagShortResponseTo toShort(Tag tag) {
        return new TagShortResponseTo(tag.getId(), tag.getName());
    }
}