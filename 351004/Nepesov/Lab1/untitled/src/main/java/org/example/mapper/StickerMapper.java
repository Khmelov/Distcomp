package org.example.mapper;

import org.example.dto.StickerRequestTo;
import org.example.dto.StickerResponseTo;
import org.example.model.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo requestTo);
    StickerResponseTo toResponse(Sticker entity);
}