package org.example.task340.discussion.kafka;

import org.example.task340.discussion.dto.MessageRequestTo;
import org.example.task340.discussion.dto.MessageResponseTo;
import org.example.task340.discussion.model.MessageState;
import org.example.task340.discussion.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KafkaMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private final MessageService messageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MessageModerationService moderationService;

    @Value("${kafka.topic.out:OutTopic}")
    private String outTopic;

    public KafkaMessageConsumer(MessageService messageService,
                               KafkaTemplate<String, Object> kafkaTemplate,
                               MessageModerationService moderationService) {
        this.messageService = messageService;
        this.kafkaTemplate = kafkaTemplate;
        this.moderationService = moderationService;
    }

    @KafkaListener(topics = "${kafka.topic.in:InTopic}", groupId = "${spring.kafka.consumer.group-id:discussion-group}")
    public void consume(@Payload KafkaMessageRequest request,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                       Acknowledgment acknowledgment) {
        log.info("Received request: operation={}, requestId={}", request.getOperation(), request.getRequestId());

        try {
            KafkaMessageResponse response = processRequest(request);
            kafkaTemplate.send(outTopic, key, response);
            log.info("Sent response: requestId={}, operation={}", response.getRequestId(), response.getOperation());
        } catch (Exception e) {
            log.error("Error processing request: requestId={}", request.getRequestId(), e);
            KafkaMessageResponse errorResponse = new KafkaMessageResponse();
            errorResponse.setRequestId(request.getRequestId());
            errorResponse.setOperation(request.getOperation());
            errorResponse.setError(e.getMessage());
            kafkaTemplate.send(outTopic, key, errorResponse);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    private KafkaMessageResponse processRequest(KafkaMessageRequest request) {
        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setOperation(request.getOperation());

        switch (request.getOperation()) {
            case "POST":
                return handlePost(request);
            case "GET":
                return handleGet(request);
            case "PUT":
                return handlePut(request);
            case "DELETE":
                return handleDelete(request);
            default:
                response.setError("Unknown operation: " + request.getOperation());
                return response;
        }
    }

    private KafkaMessageResponse handlePost(KafkaMessageRequest request) {
        MessageRequestTo messageRequest = new MessageRequestTo();
        messageRequest.setTweetId(request.getTweetId());
        messageRequest.setContent(request.getContent());
        messageRequest.setCountry(request.getCountry() != null ? request.getCountry() : "default");

        MessageResponseTo created = messageService.create(messageRequest);
        
        // Moderate the message
        MessageState moderatedState = moderationService.moderate(created.getContent());
        
        // Update state in database
        if (moderatedState != MessageState.PENDING) {
            // Get the message entity and update its state
            org.example.task340.discussion.model.Message entity = messageService.getEntityById(
                created.getCountry(), created.getTweetId(), created.getId());
            entity.setState(moderatedState);
            messageService.save(entity);
        }
        
        // Set state in response
        created.setState(moderatedState);

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setOperation("POST");
        response.setId(created.getId());
        response.setTweetId(created.getTweetId());
        response.setContent(created.getContent());
        response.setCountry(created.getCountry());
        response.setState(moderatedState.name());
        return response;
    }

    private KafkaMessageResponse handleGet(KafkaMessageRequest request) {
        if (request.getId() != null) {
            // Get by ID
            MessageResponseTo message = messageService.getById(
                request.getCountry() != null ? request.getCountry() : "default",
                request.getTweetId(),
                request.getId()
            );
            
            KafkaMessageResponse response = new KafkaMessageResponse();
            response.setRequestId(request.getRequestId());
            response.setOperation("GET");
            response.setId(message.getId());
            response.setTweetId(message.getTweetId());
            response.setContent(message.getContent());
            response.setCountry(message.getCountry());
            response.setState(message.getState() != null ? message.getState().name() : null);
            return response;
        } else {
            // Get all
            List<MessageResponseTo> messages = messageService.getAll();
            List<KafkaMessageResponse> kafkaMessages = messages.stream()
                .map(msg -> {
                    KafkaMessageResponse kafkaMsg = new KafkaMessageResponse();
                    kafkaMsg.setId(msg.getId());
                    kafkaMsg.setTweetId(msg.getTweetId());
                    kafkaMsg.setContent(msg.getContent());
                    kafkaMsg.setCountry(msg.getCountry());
                    kafkaMsg.setState(msg.getState() != null ? msg.getState().name() : null);
                    return kafkaMsg;
                })
                .collect(Collectors.toList());
            
            KafkaMessageResponse response = new KafkaMessageResponse();
            response.setRequestId(request.getRequestId());
            response.setOperation("GET");
            response.setMessages(kafkaMessages);
            return response;
        }
    }

    private KafkaMessageResponse handlePut(KafkaMessageRequest request) {
        MessageRequestTo messageRequest = new MessageRequestTo();
        messageRequest.setTweetId(request.getTweetId());
        messageRequest.setContent(request.getContent());
        messageRequest.setCountry(request.getCountry() != null ? request.getCountry() : "default");

        MessageResponseTo updated = messageService.update(
            request.getCountry() != null ? request.getCountry() : "default",
            request.getTweetId(),
            request.getId(),
            messageRequest
        );

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setOperation("PUT");
        response.setId(updated.getId());
        response.setTweetId(updated.getTweetId());
        response.setContent(updated.getContent());
        response.setCountry(updated.getCountry());
        response.setState(updated.getState() != null ? updated.getState().name() : null);
        return response;
    }

    private KafkaMessageResponse handleDelete(KafkaMessageRequest request) {
        messageService.delete(
            request.getCountry() != null ? request.getCountry() : "default",
            request.getTweetId(),
            request.getId()
        );

        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(request.getRequestId());
        response.setOperation("DELETE");
        return response;
    }
}

