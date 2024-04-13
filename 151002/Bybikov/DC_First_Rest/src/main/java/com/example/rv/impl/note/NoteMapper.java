package com.example.rv.impl.note;

import java.util.List;

public interface NoteMapper {
    NoteRequestTo noteToRequestTo(Note note);

    List<NoteRequestTo> noteToRequestTo(Iterable<Note> notes);

    Note dtoToEntity(NoteRequestTo noteRequestTo);

    List<Note> dtoToEntity(Iterable<NoteRequestTo> noteRequestTos);

    NoteResponseTo noteToResponseTo(Note note);

    List<NoteResponseTo> noteToResponseTo(Iterable<Note> notes);
}
