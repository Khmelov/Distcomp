package by.bsuir.rest.service;

import java.util.List;

public record StorySearchCriteria(
        List<String> markerNames,
        List<Long> markerIds,
        String login,
        String title,
        String content
) {

    public boolean unfiltered() {
        return empty(markerNames) && empty(markerIds) && blank(login) && blank(title) && blank(content);
    }

    private static boolean empty(List<?> values) {
        return values == null || values.isEmpty();
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
