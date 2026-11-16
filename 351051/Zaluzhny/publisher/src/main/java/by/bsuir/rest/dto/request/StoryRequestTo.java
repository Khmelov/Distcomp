package by.bsuir.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StoryRequestTo {

    private Long id;

    @NotNull(message = "is required")
    private Long userId;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 64, message = "length must be between 2 and 64")
    private String title;

    @NotBlank(message = "must not be blank")
    @Size(min = 4, max = 2048, message = "length must be between 4 and 2048")
    private String content;

    private List<Long> markerIds;

    @JsonAlias("markerNames")
    @JsonDeserialize(using = StoryMarkersDeserializer.class)
    private List<MarkerRequestTo> markers;
}
