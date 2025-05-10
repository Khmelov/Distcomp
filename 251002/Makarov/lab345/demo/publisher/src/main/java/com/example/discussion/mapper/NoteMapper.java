package com.example.discussion.mapper;

import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.model.Note;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note toEntity(NoteRequestTo dto);
}
