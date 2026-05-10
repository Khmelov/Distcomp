package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.request.NoteRequestTo;
import by.bsuir.task310.dto.response.NoteResponseTo;
import by.bsuir.task310.entity.Note;

public final class NoteMapper {
    private NoteMapper() {
    }

    public static Note toEntity(NoteRequestTo request) {
        return new Note(request.id(), request.storyId(), request.content());
    }

    public static NoteResponseTo toResponse(Note note) {
        return new NoteResponseTo(note.getId(), note.getStoryId(), note.getContent());
    }
}
