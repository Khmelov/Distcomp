package com.example.rest.mapper;

import com.example.rest.dto.TagRequestTo;
import com.example.rest.dto.TagResponseTo;
import com.example.rest.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toEntity(TagRequestTo tagRequestTo);
    TagResponseTo toResponse(Tag tag);
}
