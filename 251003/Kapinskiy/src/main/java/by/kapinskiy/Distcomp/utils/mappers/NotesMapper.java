package by.kapinskiy.Distcomp.utils.mappers;

import by.kapinskiy.Distcomp.DTOs.Requests.NoteRequestDTO;

import by.kapinskiy.Distcomp.DTOs.Responses.NoteResponseDTO;
import by.kapinskiy.Distcomp.models.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotesMapper {

    @Mapping(target = "issueId", expression = "java(note.getIssue().getId())")
    NoteResponseDTO toNoteResponse(Note note);
    List<NoteResponseDTO> toNoteResponseList(List<Note> notes);
    Note toNote(NoteRequestDTO noteRequestDTO);
}
