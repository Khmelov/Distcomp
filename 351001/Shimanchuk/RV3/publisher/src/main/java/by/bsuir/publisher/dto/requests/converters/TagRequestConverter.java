package by.bsuir.publisher.dto.requests.converters;

import by.bsuir.publisher.domain.Tag;
import by.bsuir.publisher.dto.requests.TagRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagRequestConverter {
    Tag fromDto(TagRequestDto tag);
}
