package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.PostState;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseTo implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("newsId")
    private Long newsId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("state")
    private PostState state;
}