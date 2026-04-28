package org.discussion.other.record;


import org.discussion.dto.comment.CommentResponseTo;

import java.util.List;
import java.util.UUID;

public record CommentResponseRecord(
        UUID id,
        List<CommentResponseTo> data,
        String error
) {}
