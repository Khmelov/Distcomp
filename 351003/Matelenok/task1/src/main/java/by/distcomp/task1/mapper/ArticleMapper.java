package by.distcomp.task1.mapper;

import by.distcomp.task1.dto.ArticleRequestTo;
import by.distcomp.task1.dto.ArticleResponseTo;
import by.distcomp.task1.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface ArticleMapper {

    Article toEntity(ArticleRequestTo request);

    @Mapping(source = "user.id", target = "userId")
    ArticleResponseTo toResponse(Article article);

}
