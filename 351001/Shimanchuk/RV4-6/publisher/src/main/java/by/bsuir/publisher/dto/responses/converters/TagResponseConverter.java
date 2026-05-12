package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.Tag;
import by.bsuir.publisher.dto.responses.TagResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagResponseConverter {
    TagResponseDto toDto(Tag tag);
}
