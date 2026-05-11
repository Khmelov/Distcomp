package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.News;
import by.bsuir.publisher.dto.responses.NewsResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NewsResponseConverter.class)
public interface CollectionNewsResponseConverter {
    List<NewsResponseDto> toListDto(List<News> tags);
}
