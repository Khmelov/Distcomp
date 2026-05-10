package by.tracker.rest_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class TweetResponseDto {
    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private String created;
    private String modified;
    private List<Long> markerIds;
}