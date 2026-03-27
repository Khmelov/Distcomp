package org.polozkov.other.record;

import java.util.UUID;
import org.polozkov.dto.comment.CommentResponseTo;

public record CommentResponseRecord(
        UUID id,
        CommentResponseTo data,
        String error
) {}
