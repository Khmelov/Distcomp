package com.sergey.orsik.dto.kafka;

import com.sergey.orsik.dto.request.CommentRequestTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentTransportRequest {

    /** Non-null for synchronous operations expecting a reply on OutTopic. */
    private String correlationId;

    private CommentTransportOperation operation;

    private Long commentId;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDir;
    private Long tweetId;
    private String content;

    private CommentRequestTo body;
}
