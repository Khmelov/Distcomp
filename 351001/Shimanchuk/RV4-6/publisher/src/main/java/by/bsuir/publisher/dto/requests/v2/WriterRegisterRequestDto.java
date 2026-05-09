package by.bsuir.publisher.dto.requests.v2;

import by.bsuir.publisher.domain.Role;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriterRegisterRequestDto {

    @NotBlank
    @Size(min = 3, max = 32)
    private String login;

    @NotBlank
    @Size(min = 6, max = 128)
    private String password;

    @NotBlank
    @Size(min = 1, max = 64)
    @JsonAlias({"firstName"})
    private String firstname;

    @NotBlank
    @Size(min = 1, max = 64)
    @JsonAlias({"lastName"})
    private String lastname;

    private Role role;
}
