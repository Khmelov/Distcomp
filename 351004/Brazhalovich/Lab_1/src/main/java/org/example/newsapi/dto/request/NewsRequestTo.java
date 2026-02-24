package org.example.newsapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class NewsRequestTo {
    //@JsonProperty("user") // Тестер будет слать "user", а Jackson запишет это в userId
    @NotNull
    private Long userId;

    @Size(min = 2, max = 64)
    private String title;

    @Size(min = 4, max = 2048)
    private String content;

    //@JsonProperty("marker")
    private Set<Long> markerIds;

    // Джексон увидит этот метод и положит данные из JSON-поля "user" в твой "userId"
    public void setUser(Long user) {
        this.userId = user;
    }

    // Джексон увидит этот метод и положит данные из JSON-поля "marker" в твой "markerIds"
    public void setMarker(java.util.Set<Long> marker) {
        System.out.println(">>> RECEIVED MARKER IDs FOR NEWS: " + marker);
        this.markerIds = marker;
    }
}