package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NoteMapper {
    NoteResponseTo out(Note note);

    Note in(NoteRequestTo note);
}
