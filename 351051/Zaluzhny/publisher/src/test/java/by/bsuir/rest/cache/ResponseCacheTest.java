package by.bsuir.rest.cache;

import by.bsuir.rest.dto.MessageState;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.dto.response.StoryResponseTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseCacheTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final MemoryStringCache store = new MemoryStringCache();
    private final ResponseCache cache = new ResponseCache(store, objectMapper);

    @Test
    void storesStoryAndDropsIt() {
        StoryResponseTo story = new StoryResponseTo();
        story.setId(4L);
        story.setUserId(1L);
        story.setTitle("title");
        story.setContent("text");
        story.setCreated(Instant.parse("2026-11-16T09:00:00.123456Z"));
        story.setModified(story.getCreated());
        story.setMarkerIds(List.of(2L, 3L));

        cache.put(ResponseCache.STORY, story.getId(), story);

        StoryResponseTo loaded = cache.get(ResponseCache.STORY, 4L, StoryResponseTo.class).orElseThrow();
        assertThat(loaded.getTitle()).isEqualTo("title");
        assertThat(loaded.getCreated()).isEqualTo(story.getCreated());
        assertThat(loaded.getMarkerIds()).containsExactly(2L, 3L);

        cache.evict(ResponseCache.STORY, 4L);
        assertThat(cache.get(ResponseCache.STORY, 4L, StoryResponseTo.class)).isEmpty();
    }

    @Test
    void storesMessageListAndState() {
        MessageResponseTo message = new MessageResponseTo();
        message.setId(8L);
        message.setStoryId(3L);
        message.setContent("hello");
        message.setState(MessageState.APPROVE);
        cache.put(ResponseCache.MESSAGE, ResponseCache.ALL, List.of(message));

        List<MessageResponseTo> loaded = cache.getList(ResponseCache.MESSAGE, ResponseCache.ALL, MessageResponseTo.class)
                .orElseThrow();
        assertThat(loaded).hasSize(1);
        assertThat(loaded.getFirst().getState()).isEqualTo(MessageState.APPROVE);
    }

    @Test
    void brokenJsonIsTreatedAsMiss() {
        store.put("distcomp:user:1", "{");
        assertThat(cache.get(ResponseCache.USER, 1L, MessageResponseTo.class)).isEmpty();
        assertThat(store.get("distcomp:user:1")).isNull();
    }
}
