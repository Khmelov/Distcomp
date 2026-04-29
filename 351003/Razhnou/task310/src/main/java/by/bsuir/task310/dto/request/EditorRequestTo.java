package by.bsuir.task310.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EditorRequestTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname
) {
}
