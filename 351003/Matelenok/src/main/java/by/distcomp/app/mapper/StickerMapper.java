package by.distcomp.app.mapper;

import by.distcomp.app.dto.StickerRequestTo;
import by.distcomp.app.dto.StickerResponseTo;
import by.distcomp.app.model.Sticker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    Sticker toEntity(StickerRequestTo dto);

    StickerResponseTo toResponse(Sticker sticker);
}
