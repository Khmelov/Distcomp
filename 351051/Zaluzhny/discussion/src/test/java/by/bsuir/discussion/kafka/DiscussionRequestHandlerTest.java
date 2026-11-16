package by.bsuir.discussion.kafka;

import by.bsuir.discussion.dto.MessageRequestTo;
import by.bsuir.discussion.dto.MessageResponseTo;
import by.bsuir.discussion.exception.ApiException;
import by.bsuir.discussion.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscussionRequestHandlerTest {

    @Mock
    private MessageService messages;

    @Mock
    private KafkaTemplate<String, MessageEvent> kafkaTemplate;

    private DiscussionRequestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DiscussionRequestHandler(messages, kafkaTemplate, "OutTopic");
    }

    @Test
    void createIsSavedAndDoesNotReply() {
        MessageEvent request = new MessageEvent();
        request.setOperation(MessageEvent.CREATE);
        request.setId(4L);
        request.setStoryId(10L);
        request.setContent("hello");

        handler.handle(request);

        ArgumentCaptor<MessageRequestTo> body = ArgumentCaptor.forClass(MessageRequestTo.class);
        verify(messages).create(body.capture());
        assertThat(body.getValue().getId()).isEqualTo(4L);
        assertThat(body.getValue().getStoryId()).isEqualTo(10L);
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void getRepliesOnOutTopicWithStoryKey() {
        when(messages.getById(4L)).thenReturn(new MessageResponseTo(4L, 10L, "hello"));
        MessageEvent request = new MessageEvent();
        request.setOperation(MessageEvent.GET);
        request.setCorrelationId("c-1");
        request.setId(4L);
        request.setStoryId(10L);

        handler.handle(request);

        ArgumentCaptor<MessageEvent> reply = ArgumentCaptor.forClass(MessageEvent.class);
        verify(kafkaTemplate).send(eq("OutTopic"), eq("10"), reply.capture());
        assertThat(reply.getValue().getCorrelationId()).isEqualTo("c-1");
        assertThat(reply.getValue().getMessages()).extracting(MessageResponseTo::getId).containsExactly(4L);
    }

    @Test
    void missingMessageBecomesErrorReply() {
        when(messages.getById(9L)).thenThrow(ApiException.notFound(9L));
        MessageEvent request = new MessageEvent();
        request.setOperation(MessageEvent.GET);
        request.setCorrelationId("c-2");
        request.setId(9L);

        handler.handle(request);

        ArgumentCaptor<MessageEvent> reply = ArgumentCaptor.forClass(MessageEvent.class);
        verify(kafkaTemplate).send(eq("OutTopic"), eq(null), reply.capture());
        assertThat(reply.getValue().getErrorCode()).isEqualTo(40401);
    }

    @Test
    void listByStoryUsesStoryKey() {
        when(messages.findByStoryId(10L)).thenReturn(List.of(new MessageResponseTo(4L, 10L, "hello")));
        MessageEvent request = new MessageEvent();
        request.setOperation(MessageEvent.GET_BY_STORY);
        request.setCorrelationId("c-3");
        request.setStoryId(10L);

        handler.handle(request);

        verify(kafkaTemplate).send(eq("OutTopic"), eq("10"), any());
    }
}
