package by.kopvzakone.distcomp.dto;

import by.kopvzakone.distcomp.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {
    TagResponseTo out(Tag editor);

    Tag in(TagRequestTo editor);
}
