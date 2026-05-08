package com.example.publisher.mapper;

import com.example.publisher.dto.request.StickerRequestTo;
import com.example.publisher.dto.response.StickerResponseTo;
import com.example.publisher.model.Sticker;
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