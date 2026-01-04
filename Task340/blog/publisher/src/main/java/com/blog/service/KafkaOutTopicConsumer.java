package com.blog.service;

import com.blog.dto.response.MessageResponseTo;
import com.blog.model.Message;
import com.blog.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class KafkaOutTopicConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaOutTopicConsumer.class);

    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;

    @Autowired
    public KafkaOutTopicConsumer(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Регистрируем модуль для LocalDateTime
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "${kafka.topic.out.name:OutTopic}", groupId = "publisher-group")
    @Transactional
    public void consumeOutTopicMessage(Map<String, Object> data) {
        try {
            logger.info("📥 Получен ответ из OutTopic: {}", data);

            // Преобразуем Map в MessageResponseTo
            MessageResponseTo response = objectMapper.convertValue(data, MessageResponseTo.class);

            logger.info("🔄 Обработка ответа для message ID: {}, state: {}",
                    response.getId(), response.getState());

            if (response.getId() == null) {
                logger.error("❌ Ответ не содержит ID");
                return;
            }

            // Ищем сообщение в локальной БД
            Optional<Message> existingOpt = messageRepository.findById(response.getId());

            if (existingOpt.isPresent()) {
                Message message = existingOpt.get();
                // Обновляем статус из ответа discussion
                message.setState(response.getState());
                message.setModified(LocalDateTime.now());

                // Если content изменился, обновляем его
                if (response.getContent() != null && !response.getContent().equals("[DELETED]")) {
                    message.setContent(response.getContent());
                }

                messageRepository.save(message);
                logger.info("✅ Обновлено локальное сообщение ID: {} с состоянием: {}",
                        response.getId(), response.getState());
            } else {
                logger.warn("⚠️ Сообщение ID: {} не найдено в локальной БД", response.getId());

                // Создаем новую запись
                Message newMessage = new Message();
                newMessage.setId(response.getId());
                newMessage.setTopicId(response.getTopicId());
                newMessage.setContent(response.getContent());
                newMessage.setEditorId(response.getEditorId());
                newMessage.setCountry(response.getCountry());
                newMessage.setState(response.getState());
                newMessage.setCreated(response.getCreated() != null ? response.getCreated() : LocalDateTime.now());
                newMessage.setModified(LocalDateTime.now());
                messageRepository.save(newMessage);
                logger.info("📝 Создана новая запись для сообщения ID: {}", response.getId());
            }

        } catch (Exception e) {
            logger.error("❌ Ошибка обработки Kafka ответа: {}", e.getMessage(), e);
            logger.error("Данные: {}", data);
        }
    }
}