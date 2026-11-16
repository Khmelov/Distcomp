package by.bsuir.rest.kafka;

public final class StoryKey {

    private StoryKey() {
    }

    public static String of(Long storyId) {
        return storyId == null ? null : Long.toString(storyId);
    }
}
