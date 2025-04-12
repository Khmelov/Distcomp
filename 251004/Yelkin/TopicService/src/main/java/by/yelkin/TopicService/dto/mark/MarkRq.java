package by.yelkin.TopicService.dto.mark;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkRq {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 32, message = "Name size must be between 2 and 64 chars")
    private String name;
}
