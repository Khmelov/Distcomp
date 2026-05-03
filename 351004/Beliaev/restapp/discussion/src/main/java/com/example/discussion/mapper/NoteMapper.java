package com.example.discussion.mapper;

import com.example.discussion.dto.request.NoteRequestTo;
import com.example.discussion.dto.response.NoteResponseTo;
import com.example.discussion.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Mapping(source = "articleId", target = "articleId")
    Note toEntity(NoteRequestTo request);
    @Mapping(source = "articleId", target = "articleId")
    NoteResponseTo toResponse(Note entity);
    void updateEntityFromDto(NoteRequestTo request, @MappingTarget Note entity);
}