package by.bsuir.task310.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TagRequestTo(
        Long id,
        String name
) {
}
