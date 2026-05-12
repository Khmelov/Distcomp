package by.bsuir.romamuhtasarov.api;

import by.bsuir.romamuhtasarov.impl.bean.Tag;
import by.bsuir.romamuhtasarov.impl.dto.TagRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.TagResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    Tag TagResponseToToTag(TagResponseTo responseTo);

    Tag TagRequestToToTag(TagRequestTo requestTo);

    TagRequestTo TagToTagRequestTo(Tag Tag);

    TagResponseTo TagToTagResponseTo(Tag Tag);
}