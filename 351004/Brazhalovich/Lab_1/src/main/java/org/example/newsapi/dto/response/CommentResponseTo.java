package org.example.newsapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentResponseTo {

    private Long id;

    //@JsonProperty("news")
    private Long newsId;

    private String content;

    public Long getNews() {
        return this.newsId;
    }
}