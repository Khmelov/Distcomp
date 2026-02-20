package org.polozkov.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseTo {

    private Long id;
    private Long issueId;

    @JsonProperty("content")
    private String content;

    private LocalDateTime created;
    private LocalDateTime modified;
}
