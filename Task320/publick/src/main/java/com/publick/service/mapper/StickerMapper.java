package com.publick.service.mapper;

import com.publick.dto.StickerRequestTo;
import com.publick.dto.StickerResponseTo;
import com.publick.entity.Sticker;
import org.springframework.stereotype.Component;

@Component
public class StickerMapper {

    public Sticker toEntity(StickerRequestTo dto) {
        if (dto == null) {
            return null;
        }
        Sticker sticker = new Sticker(dto.getName());
        return sticker;
    }

    public StickerResponseTo toResponse(Sticker entity) {
        if (entity == null) {
            return null;
        }
        StickerResponseTo response = new StickerResponseTo();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }

    public void updateEntityFromDto(StickerRequestTo dto, Sticker entity) {
        if (dto != null && entity != null) {
            entity.setName(dto.getName());
        }
    }
}