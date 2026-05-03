package com.example.demo.consumer;

import com.example.demo.dto.requests.MessageKafkaRequestTo;
import com.example.demo.dto.responses.MessageKafkaResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.Message;
import com.example.demo.model.MessageKey;
import com.example.demo.producer.MessageResultProducer;
import com.example.demo.service.MessageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageRequestConsumer {
    private final MessageService messageService;
    private final MessageResultProducer resultProducer;

    public MessageRequestConsumer(MessageService messageService, MessageResultProducer resultProducer) {
        this.messageService = messageService;
        this.resultProducer = resultProducer;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void consume(MessageKafkaRequestTo request) {
        MessageResponseTo saved = messageService.saveFromKafka(request);
        String newState = moderate(request.getContent());

        if (!"PENDING".equals(newState)) {
            messageService.updateState(request.getIssueId(), request.getId(), newState);
        }
        MessageKafkaResponseTo response = new MessageKafkaResponseTo(
                request.getId(), request.getIssueId(), request.getContent(), newState
        );
        resultProducer.send(response);
    }

    private String moderate(String content) {
        if (content.toLowerCase().contains("spam") || content.toLowerCase().contains("badword")) {
            return "DECLINE";
        }
        return "APPROVE";
    }
}
