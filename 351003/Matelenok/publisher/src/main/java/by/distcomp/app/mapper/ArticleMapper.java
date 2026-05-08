package by.distcomp.app.mapper;

import by.distcomp.app.model.Article;
import by.distcomp.app.dto.ArticleRequestTo;
import by.distcomp.app.dto.ArticleResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "stickers", ignore = true)
    Article toEntity(ArticleRequestTo request);

    @Mapping(source = "user.id", target = "userId")
    ArticleResponseTo toResponse(Article article);
}
