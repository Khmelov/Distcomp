package by.liza.discussion.mapper;

import by.liza.discussion.dto.request.NoteRequestTo;
import by.liza.discussion.dto.response.NoteResponseTo;
import by.liza.discussion.model.Note;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    Note toEntity(NoteRequestTo requestTo);

    NoteResponseTo toResponse(Note note);

    List<NoteResponseTo> toResponseList(List<Note> notes);
}