package com.blog.discussion.service;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    @Autowired
    private com.blog.discussion.service.impl.MessageServiceImpl messageService;

    @KafkaListener(topics = "${kafka.topic.in.name}", groupId = "discussion-group")
    public void consumeMessage(@Payload MessageRequestTo messageRequest) {
        try {
            logger.info("🎯 ========== ПОЛУЧЕНО СООБЩЕНИЕ ИЗ KAFKA ==========");
            logger.info("📨 Топик: ${kafka.topic.in.name}");
            logger.info("🆔 Message ID: {}", messageRequest.getId());
            logger.info("📝 Content: {}", messageRequest.getContent());
            logger.info("🏷️ Topic ID: {}", messageRequest.getTopicId());
            logger.info("👤 Editor ID: {}", messageRequest.getEditorId());
            logger.info("🌍 Country: {}", messageRequest.getCountry());
            logger.info("📊 State: {}", messageRequest.getState());
            logger.info("=================================================");

            // Передаем на обработку
            messageService.processIncomingMessage(messageRequest);

        } catch (Exception e) {
            logger.error("❌ ========== ОШИБКА ОБРАБОТКИ KAFKA СООБЩЕНИЯ ==========");
            logger.error("💥 Ошибка: {}", e.getMessage(), e);
            logger.error("========================================================");
        }
    }
}