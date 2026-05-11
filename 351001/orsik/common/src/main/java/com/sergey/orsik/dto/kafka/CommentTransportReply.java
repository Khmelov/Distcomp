package com.sergey.orsik.dto.kafka;

import com.sergey.orsik.dto.response.CommentResponseTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentTransportReply {

    private String correlationId;

    private boolean error;

    private String errorMessage;

    /** When error is true and set, helps the publisher map to domain exceptions. */
    private String errorEntityName;

    private Long errorEntityId;

    private CommentResponseTo comment;

    private List<CommentResponseTo> comments;
}
