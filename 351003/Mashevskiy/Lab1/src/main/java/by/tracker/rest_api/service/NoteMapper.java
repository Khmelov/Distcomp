package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.NoteRequestTo;
import by.tracker.rest_api.dto.NoteResponseTo;
import by.tracker.rest_api.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public Note toEntity(NoteRequestTo request) {
        Note note = new Note();
        note.setId(request.getId());
        note.setTweetId(request.getTweetId());
        note.setContent(request.getContent());
        return note;
    }

    public NoteResponseTo toResponse(Note entity) {
        NoteResponseTo response = new NoteResponseTo();
        response.setId(entity.getId());
        response.setTweetId(entity.getTweetId());
        response.setContent(entity.getContent());
        return response;
    }
}