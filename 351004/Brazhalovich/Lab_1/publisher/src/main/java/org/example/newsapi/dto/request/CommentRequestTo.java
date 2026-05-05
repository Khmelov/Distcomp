package org.example.newsapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestTo {

    //@JsonProperty("news")
    @NotNull
    private Long newsId;

    @Size(min = 2, max = 2048)
    private String content;

    public void setNews(Long news) {
        this.newsId = news;
    }
}