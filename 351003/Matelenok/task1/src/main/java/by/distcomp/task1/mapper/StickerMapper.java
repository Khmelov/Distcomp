package by.distcomp.task1.mapper;

import by.distcomp.task1.dto.StickerRequestTo;
import by.distcomp.task1.dto.StickerResponseTo;
import by.distcomp.task1.model.Sticker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo dto);

    StickerResponseTo toResponse(Sticker sticker);
}
