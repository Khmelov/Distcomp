package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.Tag;
import by.bsuir.publisher.dto.responses.TagResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = TagResponseConverter.class)
public interface CollectionTagResponseConverter {
    List<TagResponseDto> toListDto(List<Tag> tags);
}
