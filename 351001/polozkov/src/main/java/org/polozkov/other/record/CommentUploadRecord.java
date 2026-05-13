package org.polozkov.other.record;

import org.polozkov.dto.comment.CommentDiscussionRequest;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.other.enums.RequestMethod;

import java.util.UUID;

public record CommentUploadRecord(UUID id, RequestMethod method, CommentDiscussionRequest data) {
}
