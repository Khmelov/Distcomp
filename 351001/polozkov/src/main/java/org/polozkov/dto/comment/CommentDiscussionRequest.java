package org.polozkov.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDiscussionRequest {
    private Long id;
    private Long issueId;
    private String country;
    private String content;

    public CommentDiscussionRequest(CommentRequestTo dto) {
        issueId = dto.getIssueId();
        content = dto.getContent();
    }
}