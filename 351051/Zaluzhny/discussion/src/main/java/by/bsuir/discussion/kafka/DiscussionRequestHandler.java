package by.bsuir.discussion.kafka;

import by.bsuir.discussion.dto.MessageRequestTo;
import by.bsuir.discussion.dto.MessageResponseTo;
import by.bsuir.discussion.exception.ApiException;
import by.bsuir.discussion.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class DiscussionRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(DiscussionRequestHandler.class);

    private final MessageService messages;
    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;
    private final String outTopic;

    public DiscussionRequestHandler(MessageService messages,
                                    KafkaTemplate<String, MessageEvent> kafkaTemplate,
                                    @Value("${discussion.topics.out}") String outTopic) {
        this.messages = messages;
        this.kafkaTemplate = kafkaTemplate;
        this.outTopic = outTopic;
    }

    public void handle(MessageEvent request) {
        if (request == null || request.getOperation() == null) {
            return;
        }
        try {
            switch (request.getOperation()) {
                case MessageEvent.CREATE -> messages.create(body(request));
                case MessageEvent.GET -> reply(request, List.of(messages.getById(request.getId())), key(request));
                case MessageEvent.GET_ALL -> reply(request, messages.getAll(), null);
                case MessageEvent.GET_BY_STORY -> reply(request, messages.findByStoryId(request.getStoryId()), key(request));
                case MessageEvent.UPDATE -> reply(request, List.of(messages.update(request.getId(), body(request))), key(request));
                case MessageEvent.DELETE -> {
                    messages.delete(request.getId());
                    reply(request, List.of(), key(request));
                }
                default -> log.warn("Unknown discussion operation {}", request.getOperation());
            }
        } catch (ApiException exception) {
            if (MessageEvent.CREATE.equals(request.getOperation())) {
                log.warn("Create rejected: {}", exception.getMessage());
                return;
            }
            replyError(request, exception.getErrorCode(), exception.getMessage(), key(request));
        } catch (RuntimeException exception) {
            log.error("Discussion request failed", exception);
            if (!MessageEvent.CREATE.equals(request.getOperation())) {
                replyError(request, 50000, "Internal server error", key(request));
            }
        }
    }

    private MessageRequestTo body(MessageEvent request) {
        MessageRequestTo body = new MessageRequestTo();
        body.setId(request.getId());
        body.setStoryId(request.getStoryId());
        body.setContent(request.getContent());
        return body;
    }

    private void reply(MessageEvent request, List<MessageResponseTo> messages, String key) {
        MessageEvent response = envelope(request);
        response.setMessages(messages);
        send(key, response);
    }

    private void replyError(MessageEvent request, int errorCode, String errorMessage, String key) {
        MessageEvent response = envelope(request);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        send(key, response);
    }

    private MessageEvent envelope(MessageEvent request) {
        MessageEvent response = new MessageEvent();
        response.setOperation(request.getOperation());
        response.setCorrelationId(request.getCorrelationId());
        response.setId(request.getId());
        response.setStoryId(request.getStoryId());
        return response;
    }

    private void send(String key, MessageEvent response) {
        kafkaTemplate.send(outTopic, key, response);
    }

    private String key(MessageEvent request) {
        Long storyId = request.getStoryId();
        return storyId == null ? null : Long.toString(storyId);
    }
}
