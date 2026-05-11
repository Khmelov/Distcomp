package by.egorsosnovski.distcomp.dto;

import by.egorsosnovski.distcomp.entities.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NoteMapper {
    @Mapping(target = "id", source = "id.id")
    NoteResponseTo out(Note creator);

    @Mapping(target = "id.id", source = "id")
    @Mapping(target = "id.country", constant = "by")
    Note in(NoteRequestTo creator);
}
