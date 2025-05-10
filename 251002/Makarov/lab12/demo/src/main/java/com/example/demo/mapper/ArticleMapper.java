package com.example.demo.mapper;

import com.example.demo.dto.ArticleRequestTo;
import com.example.demo.dto.ArticleResponseTo;
import com.example.demo.model.Article;
import com.example.demo.model.Creator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "marks", ignore = true)
    Article toEntity(ArticleRequestTo dto);
    @Mapping(source = "creator.id", target = "creatorId")
    ArticleResponseTo toDto(Article entity);
    @Mapping(target = "marks", ignore = true)
    void updateEntityFromDto(ArticleRequestTo request, @MappingTarget Article entity);
    default Creator map(Long id) {
        if (id == null) return null;
        Creator creator = new Creator();
        creator.setId(id);
        return creator;
    }
}
