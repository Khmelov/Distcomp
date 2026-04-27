package by.tracker.rest_api.mapper;

import by.tracker.rest_api.dto.TweetRequestDto;
import by.tracker.rest_api.dto.TweetResponseDto;
import by.tracker.rest_api.entity.Tweet;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class TweetMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public TweetResponseDto toResponseDto(Tweet entity) {
        if (entity == null) return null;
        TweetResponseDto dto = new TweetResponseDto();
        dto.setId(entity.getId());
        dto.setCreatorId(entity.getCreator() != null ? entity.getCreator().getId() : null);
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        if (entity.getCreated() != null) {
            dto.setCreated(entity.getCreated().format(formatter));
        }
        if (entity.getModified() != null) {
            dto.setModified(entity.getModified().format(formatter));
        }
        if (entity.getMarkers() != null) {
            dto.setMarkerIds(entity.getMarkers().stream()
                    .map(marker -> marker.getId())
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public void updateEntity(TweetRequestDto dto, Tweet entity) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
    }
}