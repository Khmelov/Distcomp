package by.bsuir.rest.repository;

public record MessageFilter(Long storyId, String content) {

    public static MessageFilter empty() {
        return new MessageFilter(null, null);
    }
}
