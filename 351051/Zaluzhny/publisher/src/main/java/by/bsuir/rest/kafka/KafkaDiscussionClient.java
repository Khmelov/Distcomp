package by.bsuir.rest.kafka;

import by.bsuir.rest.client.DiscussionClient;
import by.bsuir.rest.dto.MessageState;
import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@ConditionalOnProperty(name = "discussion.client", havingValue = "kafka")
public class KafkaDiscussionClient implements DiscussionClient {

    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;
    private final MessageReplyWaiter waiter;
    private final String inTopic;
    private final ConcurrentMap<Long, Long> storyByMessage = new ConcurrentHashMap<>();

    public KafkaDiscussionClient(KafkaTemplate<String, MessageEvent> kafkaTemplate,
                                 MessageReplyWaiter waiter,
                                 @Value("${discussion.topics.in}") String inTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.waiter = waiter;
        this.inTopic = inTopic;
    }

    @KafkaListener(topics = "${discussion.topics.out}", groupId = "publisher")
    public void onReply(MessageEvent event) {
        waiter.complete(event);
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        MessageEvent event = command(MessageEvent.CREATE, request.getId(), request.getStoryId(), request.getContent());
        event.setState(MessageState.PENDING);
        kafkaTemplate.send(inTopic, StoryKey.of(request.getStoryId()), event);
        remember(request.getId(), request.getStoryId());
        return pending(request);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        MessageEvent event = command(MessageEvent.GET, id, storyByMessage.get(id), null);
        MessageResponseTo message = one(request(keyFor(id, event.getStoryId()), event));
        remember(message.getId(), message.getStoryId());
        return message;
    }

    @Override
    public List<MessageResponseTo> getAll() {
        MessageEvent event = command(MessageEvent.GET_ALL, null, null, null);
        return many(request(null, event));
    }

    @Override
    public List<MessageResponseTo> findByStoryId(Long storyId) {
        MessageEvent event = command(MessageEvent.GET_BY_STORY, null, storyId, null);
        List<MessageResponseTo> messages = many(request(StoryKey.of(storyId), event));
        messages.forEach(message -> remember(message.getId(), message.getStoryId()));
        return messages;
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        MessageEvent event = command(MessageEvent.UPDATE, id, request.getStoryId(), request.getContent());
        MessageResponseTo message = one(request(StoryKey.of(request.getStoryId()), event));
        remember(message.getId(), message.getStoryId());
        return message;
    }

    @Override
    public void delete(Long id) {
        Long storyId = storyByMessage.get(id);
        MessageEvent event = command(MessageEvent.DELETE, id, storyId, null);
        failIfError(request(keyFor(id, storyId), event));
        storyByMessage.remove(id);
    }

    private MessageEvent request(String key, MessageEvent event) {
        event.setCorrelationId(UUID.randomUUID().toString());
        CompletableFuture<MessageEvent> future = waiter.register(event.getCorrelationId());
        kafkaTemplate.send(inTopic, key, event);
        return waiter.await(future, event.getCorrelationId());
    }

    private MessageResponseTo one(MessageEvent reply) {
        failIfError(reply);
        if (reply.getMessages() == null || reply.getMessages().isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL, "Discussion returned an empty response");
        }
        return reply.getMessages().get(0);
    }

    private List<MessageResponseTo> many(MessageEvent reply) {
        failIfError(reply);
        return reply.getMessages() == null ? List.of() : reply.getMessages();
    }

    private void failIfError(MessageEvent reply) {
        if (reply.getErrorCode() == null) {
            return;
        }
        ErrorCode code = ErrorCode.fromCode(reply.getErrorCode());
        if (code == null) {
            code = ErrorCode.INTERNAL;
        }
        int statusCode = reply.getErrorCode() / 100;
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = reply.getErrorMessage() == null ? "Discussion request failed" : reply.getErrorMessage();
        throw new ApiException(status, code, message);
    }

    private String keyFor(Long id, Long storyId) {
        if (storyId != null) {
            return StoryKey.of(storyId);
        }
        return StoryKey.of(id);
    }

    private void remember(Long id, Long storyId) {
        if (id != null && storyId != null) {
            storyByMessage.put(id, storyId);
        }
    }

    private MessageResponseTo pending(MessageRequestTo request) {
        MessageResponseTo response = new MessageResponseTo();
        response.setId(request.getId());
        response.setStoryId(request.getStoryId());
        response.setContent(request.getContent());
        response.setState(MessageState.PENDING);
        return response;
    }

    private MessageEvent command(String operation, Long id, Long storyId, String content) {
        MessageEvent event = new MessageEvent();
        event.setOperation(operation);
        event.setId(id);
        event.setStoryId(storyId);
        event.setContent(content);
        return event;
    }
}
