package by.egorsosnovski.distcomp.dto;

import by.egorsosnovski.distcomp.entities.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StickerMapper {
    StickerResponseTo out(Sticker creator);

    Sticker in(StickerRequestTo creator);
}
