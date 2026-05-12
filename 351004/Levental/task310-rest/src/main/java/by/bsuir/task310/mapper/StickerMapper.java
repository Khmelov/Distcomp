package by.bsuir.task310.mapper;

import by.bsuir.task310.domain.Sticker;
import by.bsuir.task310.dto.request.StickerRequestTo;
import by.bsuir.task310.dto.response.StickerResponseTo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo dto);
    StickerResponseTo toResponse(Sticker entity);
}