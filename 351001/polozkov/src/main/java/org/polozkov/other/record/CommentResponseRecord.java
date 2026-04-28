package org.polozkov.other.record;

import java.util.List;
import java.util.UUID;
import org.polozkov.dto.comment.CommentResponseTo;

public record CommentResponseRecord(
        UUID id,
        List<CommentResponseTo> data,
        String error
) {}
