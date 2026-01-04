package com.aitor.discussion.kafka;

import com.aitor.publisher.dto.MessageRequestTo;
import com.aitor.publisher.dto.MessageResponseTo;
import com.aitor.discussion.service.MessageService;
import com.aitor.publisher.exception.EntityNotExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class KafkaConsumerService {
    private final MessageService service;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "InTopic")
    public void consume(ConsumerRecord<String, MessageRequestTo> record) {
        var key = record.key();
        var request = record.value();
        MessageResponseTo response;
        try {
            switch (key.charAt(0)){
                case 'P':
                    response = service.add(request);
                    break;
                case 'U':
                    response = service.set(Long.parseLong(key.substring(1)), request);
                    break;
                case 'D':
                    response = service.remove(Long.parseLong(key.substring(1)));
                    break;
                case 'G':
                    kafkaProducerService.sendMessage(service.getAll(), key);
                    return;
                default:
                    response = service.get(Long.parseLong(key));
                    break;
            }
        } catch (EntityNotExistsException e) {
            response = new MessageResponseTo();
        }
        kafkaProducerService.sendMessage(response, key);
    }
}
