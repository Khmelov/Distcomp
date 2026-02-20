package org.example.newsapi.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkerRequestTo {
    @Size(min = 2, max = 32)
    private String name;
}