package by.bsuir.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestTo {

    private Long id;

    @NotNull(message = "is required")
    private Long storyId;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 2048, message = "length must be between 2 and 2048")
    private String content;
}
