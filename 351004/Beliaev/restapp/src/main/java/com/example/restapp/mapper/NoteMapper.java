package com.example.restapp.mapper;

import com.example.restapp.dto.request.NoteRequestTo;
import com.example.restapp.dto.response.NoteResponseTo;
import com.example.restapp.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note toEntity(NoteRequestTo request);
    NoteResponseTo toResponse(Note entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(NoteRequestTo request, @MappingTarget Note entity);
}