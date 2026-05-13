package org.example.newsapi.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewsResponseTo {
    private Long id;


    //@JsonProperty("user")
    //@JsonAlias({"userId", "user"})
    private Long userId;

    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    //@JsonProperty("marker")
    private Set<Long> markerIds = new HashSet<>();

    public Long getUser() {
        return this.userId;
    }

    // Jackson создаст поле "marker" в JSON ответе
    public Set<Long> getMarker() {
        return this.markerIds;
    }
}