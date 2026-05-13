package org.example.mapper;

import org.example.dto.StickerRequestTo;
import org.example.dto.StickerResponseTo;
import org.example.model.Sticker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo request);
    StickerResponseTo toResponse(Sticker entity);
}