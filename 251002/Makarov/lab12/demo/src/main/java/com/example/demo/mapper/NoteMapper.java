package com.example.demo.mapper;

import com.example.demo.dto.NoteRequestTo;
import com.example.demo.dto.NoteResponseTo;
import com.example.demo.model.Article;
import com.example.demo.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Mapping(source = "articleId", target = "article")
    Note toEntity(NoteRequestTo dto);
    @Mapping(source = "article.id", target = "articleId")
    NoteResponseTo toDto(Note entity);
    @Mapping(source = "articleId", target = "article")
    void updateEntityFromDto(NoteRequestTo request, @MappingTarget Note note);

    default Article map(Long id){
        if(id == null) return null;
        Article article = new Article();
        article.setId(id);
        return article;
    }
}