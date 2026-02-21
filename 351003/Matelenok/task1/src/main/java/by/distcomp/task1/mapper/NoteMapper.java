package by.distcomp.task1.mapper;

import by.distcomp.task1.dto.NoteRequestTo;
import by.distcomp.task1.dto.NoteResponseTo;
import by.distcomp.task1.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note toEntity( NoteRequestTo dto);
    @Mapping(source = "article.id", target = "articleId")
    NoteResponseTo toResponse(Note note);
}
