package by.bsuir.task340.publisher.mapper;

import by.bsuir.task340.publisher.domain.Article;
import by.bsuir.task340.publisher.dto.request.ArticleRequestTo;
import by.bsuir.task340.publisher.dto.response.ArticleResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "creatorId", source = "creator.id")
    ArticleResponseTo toResponse(Article article);

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Article toEntity(ArticleRequestTo request);

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void update(ArticleRequestTo request, @MappingTarget Article article);
}
