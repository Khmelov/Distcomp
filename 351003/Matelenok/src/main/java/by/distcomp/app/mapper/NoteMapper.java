package by.distcomp.app.mapper;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import by.distcomp.app.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note toEntity( NoteRequestTo dto);
    @Mapping(source = "article.id", target = "articleId")
    NoteResponseTo toResponse(Note note);
}
