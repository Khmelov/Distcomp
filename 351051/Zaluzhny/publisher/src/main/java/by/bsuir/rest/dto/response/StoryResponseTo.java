package by.bsuir.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class StoryResponseTo {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Long> markerIds;
}
