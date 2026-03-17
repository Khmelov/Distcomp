package org.example.newsapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class NewsRequestTo {
    @NotNull
    private Long userId;

    @Size(min = 2, max = 64)
    private String title;

    @Size(min = 4, max = 2048)
    private String content;

    // Это поле будет заполняться через сеттеры ниже
    private Set<String> markerNames;

    // Сеттер для JSON-поля "marker"
    public void setMarker(Set<String> markerNames) {
        System.out.println(">>> setMarker called with: " + markerNames);
        this.markerNames = markerNames;
    }

    // Сеттер для JSON-поля "markers" (на случай множественного числа)
    public void setMarkers(Set<String> markerNames) {
        System.out.println(">>> setMarkers called with: " + markerNames);
        this.markerNames = markerNames;
    }
}