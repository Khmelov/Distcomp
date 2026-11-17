package by.bsuir.rest.kafka;

import by.bsuir.rest.dto.MessageState;
import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaDiscussionClientTest {

    @Mock
    private KafkaTemplate<String, MessageEvent> kafkaTemplate;

    private MessageReplyWaiter waiter;
    private KafkaDiscussionClient client;

    @BeforeEach
    void setUp() {
        waiter = new MessageReplyWaiter();
        client = new KafkaDiscussionClient(kafkaTemplate, waiter, "InTopic");
    }

    @Test
    void createReturnsPendingAndKeepsStoryInOneKey() {
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(null));
        MessageRequestTo request = new MessageRequestTo();
        request.setId(4L);
        request.setStoryId(10L);
        request.setContent("hello");

        MessageResponseTo created = client.create(request);

        assertThat(created.getState()).isEqualTo(MessageState.PENDING);
        assertThat(created.getId()).isEqualTo(4L);
        ArgumentCaptor<MessageEvent> event = ArgumentCaptor.forClass(MessageEvent.class);
        verify(kafkaTemplate).send(eq("InTopic"), eq("10"), event.capture());
        assertThat(event.getValue().getOperation()).isEqualTo(MessageEvent.CREATE);
        assertThat(event.getValue().getState()).isEqualTo(MessageState.PENDING);
    }

    @Test
    void getWaitsForReplyOnTheSameStoryKey() {
        when(kafkaTemplate.send(any(), any(), any())).thenAnswer(invocation -> {
            MessageEvent request = invocation.getArgument(2);
            MessageEvent reply = new MessageEvent();
            reply.setCorrelationId(request.getCorrelationId());
            MessageResponseTo body = new MessageResponseTo();
            body.setId(4L);
            body.setStoryId(10L);
            body.setContent("hello");
            body.setState(MessageState.APPROVE);
            reply.setMessages(List.of(body));
            waiter.complete(reply);
            return CompletableFuture.completedFuture(null);
        });
        client.create(request(4L, 10L));

        MessageResponseTo found = client.getById(4L);

        assertThat(found.getState()).isEqualTo(MessageState.APPROVE);
        ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, org.mockito.Mockito.times(2)).send(eq("InTopic"), key.capture(), any());
        assertThat(key.getAllValues()).containsExactly("10", "10");
    }

    @Test
    void sameStoryAlwaysHasTheSameKey() {
        assertThat(StoryKey.of(10L)).isEqualTo("10");
        assertThat(StoryKey.of(10L)).isEqualTo(StoryKey.of(10L));
        assertThat(StoryKey.of(11L)).isNotEqualTo(StoryKey.of(10L));
    }

    private MessageRequestTo request(Long id, Long storyId) {
        MessageRequestTo request = new MessageRequestTo();
        request.setId(id);
        request.setStoryId(storyId);
        request.setContent("hello");
        return request;
    }
}
