package by.bsuir.task330.publisher.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TagRequestTo(
        Long id,
        @NotBlank(message = "Tag name must not be blank")
        @Size(min = 2, max = 32, message = "Tag name length must be between 2 and 32")
        String name
) {
}
