package org.discussion.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestTo {
    private Long id;
    private Long issueId;
    private String country;
    private String content;
}