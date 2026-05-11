package by.bsuir.task330.publisher.mapper;

import by.bsuir.task330.publisher.domain.Sticker;
import by.bsuir.task330.publisher.dto.request.StickerRequestTo;
import by.bsuir.task330.publisher.dto.response.StickerResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo request);
    StickerResponseTo toResponse(Sticker sticker);
    void update(StickerRequestTo request, @MappingTarget Sticker sticker);
}
