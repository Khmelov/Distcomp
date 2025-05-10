package org.example.discussion.kafka;

import lombok.RequiredArgsConstructor;
import org.example.discussion.entity.Message;
import org.example.discussion.repository.MessageRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private final MessageRepo messageRepo;

    @KafkaListener(topics = "InTopic", groupId = "1")
    public void processCrudMessage(
            @Payload KafkaEvent wrapper,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("Получен CRUD запрос: operation={}, message={}, offset={}",
                wrapper.getEventType(), wrapper.getMessage(), offset);

        Message message = wrapper.getMessage();
        CrudEnum operation = wrapper.getEventType();

        try {
            switch (operation) {
                case CREATE:
                    messageRepo.create(message);
                    break;

                case GET:
                    messageRepo.findByCountryAndId(message.getCountry(), message.getId());
                    break;

                case UPDATE:
                    messageRepo.update(message);
                    break;

                case DELETE:
                    messageRepo.deleteByCountryAndId(message.getCountry(), message.getId());
                    break;

                case GET_ALL:
                    messageRepo.getAll();
                    break;

                default:
                    logger.warn("Неизвестная операция: {}", operation);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке CRUD запроса: {}", e.getMessage(), e);
        }
    }
}
