package com.example.rv.impl.note;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NoteMapperImpl implements NoteMapper {
    @Override
    public NoteRequestTo noteToRequestTo(Note note) {
        return new NoteRequestTo(
                note.getId(),
                note.getIssueId(),
                note.getContent()
        );
    }

    @Override
    public List<NoteRequestTo> noteToRequestTo(Iterable<Note> notes) {
        return StreamSupport.stream(notes.spliterator(), false)
                .map(this::noteToRequestTo)
                .collect(Collectors.toList());
    }

    @Override
    public Note dtoToEntity(NoteRequestTo noteRequestTo) {
        return new Note(
                noteRequestTo.id(),
                noteRequestTo.issueId(),
                noteRequestTo.content()
        );
    }

    @Override
    public List<Note> dtoToEntity(Iterable<NoteRequestTo> noteRequestTos) {
        return StreamSupport.stream(noteRequestTos.spliterator(), false)
                .map(this::dtoToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NoteResponseTo noteToResponseTo(Note note) {
        return new NoteResponseTo(
                note.getId(),
                note.getIssueId(),
                note.getContent());
    }

    @Override
    public List<NoteResponseTo> noteToResponseTo(Iterable<Note> notes) {
        return StreamSupport.stream(notes.spliterator(), false)
                .map(this::noteToResponseTo)
                .collect(Collectors.toList());
    }
}
