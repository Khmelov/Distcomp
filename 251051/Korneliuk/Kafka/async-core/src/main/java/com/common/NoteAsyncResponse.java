package com.common;

import java.util.List;

public record NoteAsyncResponse(
        List<NoteResponseTo> noteMessages,
        String correlationId
) {
}
