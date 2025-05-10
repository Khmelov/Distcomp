package com.example.discussion.kafka;

import com.example.discussion.dto.MessageRequestKafka;
import com.example.discussion.dto.MessageRequestTo;
import com.example.discussion.dto.MessageResponseKafka;
import com.example.discussion.dto.MessageResponseTo;
import com.example.discussion.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final MessageService messageService;
    private final KafkaProducer kafkaProducer;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void consume(MessageRequestKafka message) {
        log.info("Received message: {}", message);
        if (message.getRequestType().equals("PUT")){
            MessageResponseTo response = messageService.updateMessage(message.getRequest());
            kafkaProducer.sendResponse(new MessageResponseKafka(response,"PUT", "OK"));
        }
        else if (message.getRequestType().equals("POST")){
            MessageResponseTo response = messageService.createMessage(message.getRequest());
        }
        else if (message.getRequestType().equals("GET")){
            MessageResponseTo response = messageService.getMessageById(message.getRequest().getId());
            kafkaProducer.sendResponse(new MessageResponseKafka(response,"GET", "OK"));
        }
        else if (message.getRequestType().equals("DELETE")){
            messageService.deleteMessage(message.getRequest().getId());
            kafkaProducer.sendResponse(new MessageResponseKafka(new MessageResponseTo(),"DELETE", "OK"));
        }
    }
}
