package by.yelkin.TopicService.dto.creator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatorUpdateRq {
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotBlank(message = "Login must not be blank")
    @Size(min = 2, max = 64, message = "Login size must be between 2 and 64 chars")
    private String login;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 128, message = "Password size must be between 8 and 128 chars")
    private String password;

    @NotBlank(message = "Firstname must not be blank")
    @Size(min = 2, max = 64, message = "Firstname size must be between 2 and 64 chars")
    private String firstname;

    @NotBlank(message = "Lastname must not be blank")
    @Size(min = 2, max = 64, message = "Lastname size must be between 2 and 64 chars")
    private String lastname;
}
