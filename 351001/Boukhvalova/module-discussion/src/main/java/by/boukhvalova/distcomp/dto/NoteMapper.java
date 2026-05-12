package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.Note;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note in(NoteRequestTo request);

    NoteResponseTo out(Note entity);
}
