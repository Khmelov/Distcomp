package com.example.discussion.mapper;

import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.dto.NoteResponseTo;
import com.example.discussion.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    // Маппинг из NoteRequestTo в Note (создание новой записи)
    Note toEntity(NoteRequestTo dto);

    // Маппинг из Note в NoteResponseTo (получение ответа)
    NoteResponseTo toDto(Note entity);

    // Маппинг для обновления сущности Note из NoteRequestTo
    void updateEntityFromDto(NoteRequestTo request, @MappingTarget Note note);
}
