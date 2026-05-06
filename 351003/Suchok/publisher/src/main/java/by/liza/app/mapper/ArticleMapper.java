package by.liza.app.mapper;

import by.liza.app.dto.request.ArticleRequestTo;
import by.liza.app.dto.response.ArticleResponseTo;
import by.liza.app.model.Article;
import by.liza.app.model.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Article toEntity(ArticleRequestTo requestTo);

    @Mapping(target = "writerId", source = "writer.id")
    @Mapping(target = "markIds", source = "marks", qualifiedByName = "marksToIds")
    ArticleResponseTo toResponse(Article article);

    List<ArticleResponseTo> toResponseList(List<Article> articles);

    @Named("marksToIds")
    default List<Long> marksToIds(List<Mark> marks) {
        if (marks == null) return Collections.emptyList();
        return marks.stream().map(Mark::getId).collect(Collectors.toList());
    }
}