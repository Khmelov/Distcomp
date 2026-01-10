package com.blog.mapper;

import com.blog.dto.ReactionRequestTo;
import com.blog.dto.ReactionResponseTo;
import com.blog.entity.Article;
import com.blog.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(target = "article", source = "articleId", qualifiedByName = "articleIdToArticle")
    Reaction requestToToEntity(ReactionRequestTo request);

    @Mapping(target = "articleId", source = "article.id")
    ReactionResponseTo entityToResponseTo(Reaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "article", source = "articleId", qualifiedByName = "articleIdToArticle")
    Reaction updateEntityFromRequest(ReactionRequestTo request, @MappingTarget Reaction entity);

    @org.mapstruct.Named("articleIdToArticle")
    default Article articleIdToArticle(Long articleId) {
        if (articleId == null) {
            return null;
        }
        Article article = new Article();
        article.setId(articleId);
        return article;
    }
}