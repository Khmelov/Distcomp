package com.lab1.lab1DC.mapper;

import com.lab1.lab1DC.dto.StickerRequestTo;
import com.lab1.lab1DC.dto.StickerResponseTo;
import com.lab1.lab1DC.entity.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StickerMapper {

    StickerMapper INSTANCE = Mappers.getMapper(StickerMapper.class);

    Sticker toEntity(StickerRequestTo stickerRequestTo);

    StickerResponseTo toResponseDto(Sticker sticker);

    Sticker toEntity(StickerResponseTo stickerResponseTo);

    StickerRequestTo toRequestDto(Sticker sticker);
}
