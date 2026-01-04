package com.example.entitiesapp.mappers;

import com.example.entitiesapp.dto.request.StickerRequestTo;
import com.example.entitiesapp.dto.response.StickerResponseTo;
import com.example.entitiesapp.entities.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "articles", ignore = true)
    Sticker toEntity(StickerRequestTo dto);

    StickerResponseTo toResponseDto(Sticker entity);
}