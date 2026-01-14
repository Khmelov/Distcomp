package com.blog.service;

import com.blog.dto.request.MessageRequestTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; // Изменили на Object

    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.in.name:InTopic}")
    private String inTopic;

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(MessageRequestTo request) {
        try {
            logger.info("📤 Отправка сообщения в Kafka топик: {}", inTopic);

            // Убедимся, что все обязательные поля заполнены
            if (request.getId() == null) {
                logger.error("❌ ID сообщения null! Нельзя отправить в Kafka.");
                return;
            }

            if (request.getTopicId() == null) {
                request.setTopicId(1L);
                logger.warn("⚠️ TopicId не указан, установлен по умолчанию: 1");
            }

            if (request.getEditorId() == null) {
                request.setEditorId(1L);
                logger.warn("⚠️ EditorId не указан, установлен по умолчанию: 1");
            }

            if (request.getCountry() == null) {
                request.setCountry("global");
                logger.warn("⚠️ Country не указан, установлен по умолчанию: global");
            }

            if (request.getState() == null) {
                request.setState("PENDING");
                logger.warn("⚠️ State не указан, установлен по умолчанию: PENDING");
            }

            // Используем topicId как ключ для партиционирования (все сообщения одного topicId в одной партиции)
            String key = String.valueOf(request.getTopicId());

            // Отправляем как объект (Spring автоматически сериализует в JSON)
            kafkaTemplate.send(inTopic, key, request);

            logger.info("✅ Сообщение отправлено в Kafka. ID: {}, TopicId: {}, Key: {}",
                    request.getId(), request.getTopicId(), key);

        } catch (Exception e) {
            logger.error("❌ Ошибка отправки сообщения в Kafka: {}", e.getMessage(), e);
        }
    }
}