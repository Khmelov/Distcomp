package by.yelkin.TopicService.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicUpdateRq {
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotNull(message = "CreatorId must not be null")
    private Long creatorId;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 2, max = 64, message = "Title size must be between 2 and 64 chars")
    private String title;

    @NotBlank(message = "Content must not be blank")
    @Size(min = 4, max = 2048, message = "Content size must be between 4 and 2048 chars")
    private String content;
}
