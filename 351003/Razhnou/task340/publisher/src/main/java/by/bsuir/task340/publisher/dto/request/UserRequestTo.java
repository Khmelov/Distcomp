package by.bsuir.task340.publisher.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRequestTo(
        Long id,
        @NotBlank(message = "User login must not be blank")
        @Size(min = 2, max = 64, message = "User login length must be between 2 and 64")
        String login,
        @NotBlank(message = "User password must not be blank")
        @Size(min = 8, max = 128, message = "User password length must be between 8 and 128")
        String password,
        @NotBlank(message = "User firstname must not be blank")
        @Size(min = 2, max = 64, message = "User firstname length must be between 2 and 64")
        String firstname,
        @NotBlank(message = "User lastname must not be blank")
        @Size(min = 2, max = 64, message = "User lastname length must be between 2 and 64")
        String lastname
) {
}
