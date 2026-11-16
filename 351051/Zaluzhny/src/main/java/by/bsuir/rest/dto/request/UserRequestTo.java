package by.bsuir.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestTo {

    private Long id;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 64, message = "length must be between 2 and 64")
    private String login;

    @NotBlank(message = "must not be blank")
    @Size(min = 8, max = 128, message = "length must be between 8 and 128")
    private String password;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 64, message = "length must be between 2 and 64")
    private String firstname;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 64, message = "length must be between 2 and 64")
    private String lastname;
}
