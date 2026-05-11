package by.bsuir.task320.mapper;

import by.bsuir.task310.domain.Article;
import by.bsuir.task310.dto.request.ArticleRequestTo;
import by.bsuir.task310.dto.response.ArticleResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Article toEntity(ArticleRequestTo dto);

    ArticleResponseTo toResponse(Article entity);
}