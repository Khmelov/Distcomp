package org.discussion.other.record;

import org.discussion.dto.comment.CommentRequestTo;
import org.discussion.other.enums.RequestMethod;

import java.util.UUID;

public record CommentUploadRecord(UUID id, RequestMethod method, CommentRequestTo data) {
}
