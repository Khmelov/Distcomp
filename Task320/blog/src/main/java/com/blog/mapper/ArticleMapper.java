package com.blog.mapper;

import com.blog.dto.ArticleRequestTo;
import com.blog.dto.ArticleResponseTo;
import com.blog.entity.Article;
import com.blog.entity.Writer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "writer", source = "writerId", qualifiedByName = "writerIdToWriter")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    Article requestToToEntity(ArticleRequestTo request);

    @Mapping(target = "writerId", source = "writer.id")
    ArticleResponseTo entityToResponseTo(Article entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "writer", source = "writerId", qualifiedByName = "writerIdToWriter")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    Article updateEntityFromRequest(ArticleRequestTo request, @MappingTarget Article entity);

    @org.mapstruct.Named("writerIdToWriter")
    default Writer writerIdToWriter(Long writerId) {
        if (writerId == null) {
            return null;
        }
        Writer writer = new Writer();
        writer.setId(writerId);
        return writer;
    }
}