package by.distcomp.app.dto;

public record NoteResponseTo(
        Long id,
        Long articleId,
        String content,
        NoteState state
) { }
