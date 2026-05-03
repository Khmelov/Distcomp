package org.example.newsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Обязательно для правильной работы Jackson
@AllArgsConstructor
public class MarkerRequestTo {
    @NotBlank
    @Size(min = 2, max = 32)
    private String name;
}