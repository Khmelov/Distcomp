package com.example.rv.impl.note;

public record NoteRequestTo(
        Long id,
        Long issueId,
        String content
) {}
