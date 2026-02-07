package by.distcomp.task1.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ArticleResponseTo(

        Long id,
        String title,
        String content,
        OffsetDateTime created,
        OffsetDateTime modified,
        Long userId,
        List<NoteResponseTo> notes,
        List<StickerResponseTo> stickers
) { }
