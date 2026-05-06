package com.example.demo.mapper;

import com.example.demo.dto.request.TagRequestTo;
import com.example.demo.dto.response.TagResponseTo;
import com.example.demo.models.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toEntity(TagRequestTo tagResponseTo);
    TagResponseTo toResponse(Tag tagEntity);
    List<TagResponseTo> entityListToResponse(List<Tag> listTag);
}
