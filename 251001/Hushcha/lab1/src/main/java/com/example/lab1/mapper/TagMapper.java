package com.example.lab1.mapper;

import com.example.lab1.dto.TagRequestTo;
import com.example.lab1.dto.TagResponseTo;
import com.example.lab1.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagResponseTo toDTO(Tag tag);
    Tag toEntity(TagRequestTo tagRequestTo);
}
