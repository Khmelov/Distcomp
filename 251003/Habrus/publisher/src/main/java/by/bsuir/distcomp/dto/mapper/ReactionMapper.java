package by.bsuir.distcomp.dto.mapper;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.entity.Article;
import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.repository.ArticleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class ReactionMapper {

    @Autowired
    private ArticleRepository articleRepository;

    @Mapping(target = "articleId", source = "article", qualifiedByName = "mapArticleBack")
    public abstract ReactionResponseTo toDto(Reaction reaction);

    @Mapping(target = "article", source = "articleId", qualifiedByName = "mapArticle")
    public abstract Reaction toEntity(ReactionRequestTo reactionDTO);

    @Named("mapArticle")
    protected Article mapArticle(Long articleId) {
        return articleRepository
                .findById(articleId)
                .orElseThrow(() -> new NoSuchElementException("Article with id: " + articleId + " not found"));
    }

    @Named("mapArticleBack")
    protected Long mapArticleBack(Article article) {
        return article.getId();
    }

}
