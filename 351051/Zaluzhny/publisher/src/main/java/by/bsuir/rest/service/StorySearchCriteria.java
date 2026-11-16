package by.bsuir.rest.service;

import java.util.List;

public record StorySearchCriteria(
        List<String> markerNames,
        List<Long> markerIds,
        String login,
        String title,
        String content
) {
}
