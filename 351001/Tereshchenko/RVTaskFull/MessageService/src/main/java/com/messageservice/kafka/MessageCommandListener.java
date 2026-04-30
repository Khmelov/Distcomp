package com.messageservice.kafka;

import com.messageservice.configs.exceptionhandlerconfig.exceptions.MessageNotFoundException;
import com.messageservice.configs.exceptionhandlerconfig.exceptions.TweetNotFoundException;
import com.messageservice.dtos.MessageRequestTo;
import com.messageservice.dtos.MessageResponseTo;
import com.messageservice.services.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MessageCommandListener {

    private final MessageService messageService;
    private final KafkaTemplate<String, MessageResultEvent> kafkaTemplate;
    private final String outTopic;

    public MessageCommandListener(
            MessageService messageService,
            KafkaTemplate<String, MessageResultEvent> kafkaTemplate,
            @Value("${app.kafka.out-topic}") String outTopic) {
        this.messageService = messageService;
        this.kafkaTemplate = kafkaTemplate;
        this.outTopic = outTopic;
    }

    @KafkaListener(
            topics = "${app.kafka.in-topic}",
            groupId = "${app.kafka.discussion-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(MessageCommandEvent command) {
        MessageResultEvent result;
        try {
            result = handle(command);
        } catch (TweetNotFoundException e) {
            result = MessageResultEvent.failure(command, "BAD_REQUEST", e.getMessage());
        } catch (MessageNotFoundException e) {
            result = MessageResultEvent.failure(command, "NOT_FOUND", e.getMessage());
        } catch (RuntimeException e) {
            result = MessageResultEvent.failure(command, "INTERNAL", e.getMessage());
        }

        kafkaTemplate.send(outTopic, partitionKey(command), result);
    }

    private MessageResultEvent handle(MessageCommandEvent command) {
        return switch (command.getOperation()) {
            case CREATE -> MessageResultEvent.success(command, messageService.createMessage(toRequest(command)));
            case UPDATE -> MessageResultEvent.success(command, messageService.updateMessageById(toRequest(command), command.getMessageId()));
            case DELETE_BY_ID -> {
                messageService.deleteMessageById(command.getMessageId());
                yield MessageResultEvent.success(command);
            }
            case DELETE_BY_TWEET -> {
                messageService.deleteMessageByTweetId(command.getTweetId());
                yield MessageResultEvent.success(command);
            }
        };
    }

    private MessageRequestTo toRequest(MessageCommandEvent command) {
        MessageRequestTo request = new MessageRequestTo();
        request.setTweetId(command.getTweetId());
        request.setContent(command.getContent());
        return request;
    }

    private String partitionKey(MessageCommandEvent command) {
        Long key = command.getTweetId() != null ? command.getTweetId() : command.getMessageId();
        return Objects.toString(key, command.getCorrelationId());
    }
}
