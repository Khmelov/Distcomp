package by.egorsosnovski.distcomp.dto;

import by.egorsosnovski.distcomp.entities.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NoteMapper {
    NoteResponseTo out(Note creator);

    Note in(NoteRequestTo creator);
}
