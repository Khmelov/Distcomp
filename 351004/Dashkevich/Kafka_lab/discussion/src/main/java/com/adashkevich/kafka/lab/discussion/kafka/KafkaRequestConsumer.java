package com.adashkevich.kafka.lab.discussion.kafka;

import com.adashkevich.kafka.lab.discussion.config.KafkaTopicProperties;
import com.adashkevich.kafka.lab.discussion.dto.MessageRequestTo;
import com.adashkevich.kafka.lab.discussion.exception.ApiException;
import com.adashkevich.kafka.lab.discussion.service.MessageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaRequestConsumer {
    private final MessageService messageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties properties;

    public KafkaRequestConsumer(MessageService messageService, KafkaTemplate<String, Object> kafkaTemplate, KafkaTopicProperties properties) {
        this.messageService = messageService;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @KafkaListener(topics = "${app.kafka.in-topic}", groupId = "discussion-group", containerFactory = "kafkaRequestListenerContainerFactory")
    public void consume(KafkaRequestMessage request) {
        KafkaReplyMessage reply = new KafkaReplyMessage();
        reply.setCorrelationId(request.getCorrelationId());
        try {
            switch (request.getOperation()) {
                case CREATE -> reply.setMessage(messageService.createModerated(request.getId(), request.getNewsId(), request.getContent()));
                case READ_ALL -> reply.setMessages(messageService.getAll());
                case READ_BY_ID -> reply.setMessage(messageService.getById(request.getId()));
                case UPDATE -> reply.setMessage(messageService.update(request.getId(), toRequest(request)));
                case DELETE -> messageService.delete(request.getId());
                case READ_BY_NEWS_ID -> reply.setMessages(messageService.getByNewsId(request.getNewsId()));
                case DELETE_BY_NEWS_ID -> messageService.deleteByNewsId(request.getNewsId());
            }
            reply.setSuccess(true);
            reply.setHttpStatus(200);
        } catch (ApiException ex) {
            reply.setSuccess(false);
            reply.setHttpStatus(ex.getStatus().value());
            reply.setErrorCode(ex.getCode());
            reply.setErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            reply.setSuccess(false);
            reply.setHttpStatus(500);
            reply.setErrorCode("50000");
            reply.setErrorMessage("Internal server error");
        }
        kafkaTemplate.send(properties.getOutTopic(), request.getCorrelationId(), reply);
    }

    private MessageRequestTo toRequest(KafkaRequestMessage request) {
        MessageRequestTo dto = new MessageRequestTo();
        dto.newsId = request.getNewsId();
        dto.content = request.getContent();
        return dto;
    }
}
