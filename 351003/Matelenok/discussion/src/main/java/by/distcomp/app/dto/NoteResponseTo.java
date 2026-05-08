package by.distcomp.app.dto;

import by.distcomp.app.model.NoteState;

public record NoteResponseTo(
        Long id,
        Long articleId,
        String content,
        NoteState state
) { }
