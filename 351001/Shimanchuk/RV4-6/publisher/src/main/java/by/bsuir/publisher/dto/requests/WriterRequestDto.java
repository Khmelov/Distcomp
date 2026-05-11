package by.bsuir.publisher.dto.requests;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class WriterRequestDto extends BaseRequestDto {
    @Size(min = 3, max = 32)
    private String login;

    @Size(min = 8, max = 32)
    private String password;

    @Size(min = 3, max = 32)
    private String firstname;

    @Size(min = 3, max = 32)
    private String lastname;
}
