package by.bsuir.task361.publisher.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestTo(
        @NotBlank(message = "User login must not be blank")
        @Size(min = 2, max = 64, message = "User login length must be between 2 and 64")
        String login,
        @NotBlank(message = "User password must not be blank")
        @Size(min = 8, max = 128, message = "User password length must be between 8 and 128")
        String password
) {
}
