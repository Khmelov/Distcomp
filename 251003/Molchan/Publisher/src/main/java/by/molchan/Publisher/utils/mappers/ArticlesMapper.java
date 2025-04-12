package by.molchan.Publisher.utils.mappers;

import by.molchan.Publisher.DTOs.Requests.ArticleRequestDTO;
import by.molchan.Publisher.DTOs.Responses.ArticleResponseDTO;
import by.molchan.Publisher.models.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArticlesMapper {

    @Mapping(target = "creatorId", expression = "java(article.getCreator().getId())")
    @Mapping(target = "labels", expression = "java(article.getLabels().stream().map(label -> label.getName()).toList())")
    ArticleResponseDTO toArticleResponse(Article article);

    List<ArticleResponseDTO> toArticleResponseList(List<Article> articles);

    @Mapping(target = "labels", ignore = true)
    Article toArticle(ArticleRequestDTO articleRequestDTO);
}
