package com.example.restapp.mapper;

import com.example.restapp.dto.request.NoteRequestTo;
import com.example.restapp.dto.response.NoteResponseTo;
import com.example.restapp.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Mapping(target = "article", ignore = true) // Handled in Service
    Note toEntity(NoteRequestTo request);

    @Mapping(source = "article.id", target = "articleId")
    NoteResponseTo toResponse(Note entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "article", ignore = true) // Handled in Service
    void updateEntityFromDto(NoteRequestTo request, @MappingTarget Note entity);
}