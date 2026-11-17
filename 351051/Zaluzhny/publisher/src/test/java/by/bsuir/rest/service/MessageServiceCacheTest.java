package by.bsuir.rest.service;

import by.bsuir.rest.cache.ResponseCache;
import by.bsuir.rest.client.DiscussionClient;
import by.bsuir.rest.dto.MessageState;
import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.repository.StoryRepository;
import by.bsuir.rest.service.impl.MessageServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceCacheTest {

    @Mock
    private StoryRepository stories;

    @Mock
    private RequestValidator validator;

    private final RecordingDiscussion discussion = new RecordingDiscussion();
    private MessageService service;

    @BeforeEach
    void setUp() {
        when(stories.existsById(10L)).thenReturn(true);
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        service = new MessageServiceImpl(
                discussion,
                stories,
                validator,
                new UpdateIdResolver(),
                new MessageIds(),
                ResponseCache.memory(objectMapper));
    }

    @Test
    void readUsesCacheUntilDeleteAndDoesNotKeepPending() {
        MessageRequestTo request = new MessageRequestTo();
        request.setStoryId(10L);
        request.setContent("hello");

        MessageResponseTo created = service.create(request);
        assertThat(created.getState()).isEqualTo(MessageState.PENDING);

        MessageResponseTo first = service.getById(created.getId());
        MessageResponseTo second = service.getById(created.getId());
        assertThat(first.getState()).isEqualTo(MessageState.APPROVE);
        assertThat(second.getContent()).isEqualTo("hello");
        assertThat(discussion.reads).isEqualTo(1);

        MessageRequestTo update = new MessageRequestTo();
        update.setStoryId(10L);
        update.setContent("changed");
        assertThat(service.update(created.getId(), update).getState()).isEqualTo(MessageState.DECLINE);
        assertThat(service.getById(created.getId()).getContent()).isEqualTo("changed");
        assertThat(discussion.reads).isEqualTo(1);

        service.delete(created.getId());
        assertThatThrownBy(() -> service.getById(created.getId()))
                .isInstanceOf(ApiException.class);
        assertThat(discussion.reads).isEqualTo(2);
    }

    private static final class RecordingDiscussion implements DiscussionClient {

        private int reads;
        private MessageResponseTo stored;

        @Override
        public MessageResponseTo create(MessageRequestTo request) {
            stored = message(request.getId(), request.getStoryId(), request.getContent(), MessageState.APPROVE);
            return message(request.getId(), request.getStoryId(), request.getContent(), MessageState.PENDING);
        }

        @Override
        public MessageResponseTo getById(Long id) {
            reads++;
            if (stored == null || !stored.getId().equals(id)) {
                throw ApiException.notFound("Message", id);
            }
            return message(stored.getId(), stored.getStoryId(), stored.getContent(), stored.getState());
        }

        @Override
        public List<MessageResponseTo> getAll() {
            return List.of();
        }

        @Override
        public List<MessageResponseTo> findByStoryId(Long storyId) {
            return List.of();
        }

        @Override
        public MessageResponseTo update(Long id, MessageRequestTo request) {
            stored = message(id, request.getStoryId(), request.getContent(), MessageState.DECLINE);
            return message(stored.getId(), stored.getStoryId(), stored.getContent(), stored.getState());
        }

        @Override
        public void delete(Long id) {
            if (stored == null || !stored.getId().equals(id)) {
                throw ApiException.notFound("Message", id);
            }
            stored = null;
        }

        private static MessageResponseTo message(Long id, Long storyId, String content, MessageState state) {
            MessageResponseTo response = new MessageResponseTo();
            response.setId(id);
            response.setStoryId(storyId);
            response.setContent(content);
            response.setState(state);
            return response;
        }
    }
}
