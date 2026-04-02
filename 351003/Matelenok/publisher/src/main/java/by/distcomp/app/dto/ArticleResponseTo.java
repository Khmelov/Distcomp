package by.distcomp.app.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
@Builder(toBuilder = true)
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
