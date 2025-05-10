package com.example.discussion.service.mapper;

import com.example.discussion.dto.StickerRequestTo;
import com.example.discussion.dto.StickerResponseTo;
import com.example.discussion.model.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StickerMapper {

    Sticker toEntity(StickerRequestTo dto); // Преобразует DTO в сущность

    StickerResponseTo toDto(Sticker entity); // Преобразует сущность в DTO

    void updateEntityFromDto(StickerRequestTo dto, @MappingTarget Sticker entity); // Обновляет сущность на основе DTO
}