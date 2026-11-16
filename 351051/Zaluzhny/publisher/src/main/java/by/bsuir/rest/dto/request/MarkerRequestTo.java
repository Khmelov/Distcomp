package by.bsuir.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarkerRequestTo {

    private Long id;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 32, message = "length must be between 2 and 32")
    private String name;
}
