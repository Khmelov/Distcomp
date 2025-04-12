package com.example.rest.mapper;

import com.example.rest.dto.tag.TagRequestTo;
import com.example.rest.dto.tag.TagResponseTo;
import com.example.rest.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toEntity(TagRequestTo tagRequestTo);
    TagResponseTo toResponse(Tag tag);
}
