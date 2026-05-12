package by.bsuir.publisher.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class WriterResponseDto extends BaseResponseDto {
    private String login;

    @JsonIgnore
    private String password;

    private String firstname;
    private String lastname;
}
