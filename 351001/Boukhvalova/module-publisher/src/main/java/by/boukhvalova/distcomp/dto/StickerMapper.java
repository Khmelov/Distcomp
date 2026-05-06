package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StickerMapper {
    StickerResponseTo out(Sticker sticker);

    Sticker in(StickerRequestTo sticker);
}
