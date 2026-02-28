package com.example.restapp.mapper;

import com.example.restapp.dto.request.StickerRequestTo;
import com.example.restapp.dto.response.StickerResponseTo;
import com.example.restapp.model.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo request);
    StickerResponseTo toResponse(Sticker entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(StickerRequestTo request, @MappingTarget Sticker entity);
}