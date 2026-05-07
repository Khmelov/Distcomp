package com.example.discussion.mapper;

import com.example.discussion.dto.request.StickerRequestTo;
import com.example.discussion.dto.response.StickerResponseTo;
import com.example.discussion.model.Sticker;
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