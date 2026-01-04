package com.blog.discussion.service.impl;

import com.blog.discussion.config.IdGenerator;
import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.exception.ResourceNotFoundException;
import com.blog.discussion.mapper.MessageMapper;
import com.blog.discussion.model.Message;
import com.blog.discussion.repository.MessageRepository;
import com.blog.discussion.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private IdGenerator idGenerator;

    private static final String DEFAULT_COUNTRY = "global";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.out.name:OutTopic}")
    private String outTopicName;

    @Override
    public void processIncomingMessage(MessageRequestTo request) {
        try {
            logger.info("🔍 ========== START PROCESSING MESSAGE ==========");
            logger.info("📥 Получен запрос: {}", request);

            // Определяем параметры
            String country = (request.getCountry() != null && !request.getCountry().isEmpty())
                    ? request.getCountry()
                    : DEFAULT_COUNTRY;
            Long topicId = request.getTopicId() != null ? request.getTopicId() : 1L;
            Long editorId = request.getEditorId() != null ? request.getEditorId() : 1L;

            // Определяем ID сообщения
            Long messageId = request.getId();
            if (messageId == null) {
                messageId = idGenerator.getNextId();
                logger.warn("⚠️ ID сообщения не предоставлен, сгенерирован: {}", messageId);
            }

            // Проверяем, не является ли это DELETE запросом
            if ("DELETED".equals(request.getState())) {
                logger.info("🗑️ Обработка DELETE запроса для message ID: {}", messageId);
                try {
                    // Удаляем из Cassandra
                    messageRepository.deleteByCountryAndTopicIdAndId(country, topicId, messageId);
                    logger.info("✅ Сообщение ID {} удалено из Cassandra", messageId);

                    // Отправляем подтверждение удаления
                    MessageResponseTo deleteResponse = new MessageResponseTo();
                    deleteResponse.setId(messageId);
                    deleteResponse.setCountry(country);
                    deleteResponse.setTopicId(topicId);
                    deleteResponse.setState("DELETED");
                    deleteResponse.setAllBooleanFields();
                    kafkaTemplate.send(outTopicName, String.valueOf(messageId), deleteResponse);
                    logger.info("✅ Подтверждение удаления отправлено в OutTopic");

                } catch (Exception e) {
                    logger.error("❌ Ошибка при удалении сообщения {}: {}", messageId, e.getMessage());
                }
                return;
            }

            // Модерация контента
            logger.info("🔄 Запуск модерации...");
            String state = moderateMessage(request.getContent());
            logger.info("✅ Результат модерации: {}", state);

            // СОЗДАЕМ СООБЩЕНИЕ
            Message message = new Message();
            message.setCountry(country);
            message.setTopicId(topicId);
            message.setId(messageId);
            message.setContent(request.getContent());
            message.setEditorId(editorId);
            message.setState(state);
            message.setCreated(LocalDateTime.now());
            message.setModified(LocalDateTime.now());

            logger.info("💾 Сохранение в Cassandra: country={}, topicId={}, id={}",
                    country, topicId, messageId);

            // Сохраняем в Cassandra
            Message savedMessage = messageRepository.save(message);
            logger.info("✅ Сохранено успешно в Cassandra: ID={}", savedMessage.getId());

            // Создаем ответ
            MessageResponseTo response = messageMapper.toResponse(savedMessage);
            response.setAllBooleanFields();

            // Отправляем в Kafka
            logger.info("📤 Отправка ответа в Kafka топик: {}", outTopicName);
            String key = String.valueOf(savedMessage.getId());
            kafkaTemplate.send(outTopicName, key, response);
            logger.info("✅ Ответ отправлен в OutTopic для message ID: {}", savedMessage.getId());
            logger.info("🎉 ========== ОБРАБОТКА ЗАВЕРШЕНА ==========");

        } catch (Exception e) {
            logger.error("❌ ========== ОШИБКА ПРИ ОБРАБОТКЕ ==========");
            logger.error("💥 Ошибка: {}", e.getMessage(), e);

            try {
                // Отправляем ошибку в Kafka
                MessageResponseTo errorResponse = new MessageResponseTo();
                errorResponse.setId(request.getId());
                errorResponse.setTopicId(request.getTopicId());
                errorResponse.setContent("Error: " + e.getMessage());
                errorResponse.setState("ERROR");
                errorResponse.setAllBooleanFields();
                errorResponse.setCreated(LocalDateTime.now());
                errorResponse.setModified(LocalDateTime.now());

                kafkaTemplate.send(outTopicName, errorResponse);
                logger.error("📤 Ошибка отправлена в Kafka");
            } catch (Exception ex) {
                logger.error("❌ Не удалось отправить ошибку: {}", ex.getMessage());
            }
        }
    }

    private String moderateMessage(String content) {
        if (content == null || content.isEmpty()) {
            return "DECLINED";
        }

        String lowerContent = content.toLowerCase();

        // Список стоп-слов
        String[] stopWords = {"спам", "реклама", "viagra", "casino", "sex", "porn", "мошенничество", "обман"};

        for (String word : stopWords) {
            if (lowerContent.contains(word)) {
                logger.warn("🚫 Сообщение содержит стоп-слово: {}", word);
                return "DECLINED";
            }
        }

        // Простая проверка длины
        if (content.length() < 10) {
            logger.warn("📏 Сообщение слишком короткое: {} символов", content.length());
            return "DECLINED";
        }

        // Проверка на повторяющиеся символы
        if (hasRepeatingCharacters(content)) {
            logger.warn("🔁 Сообщение содержит повторяющиеся символы");
            return "DECLINED";
        }

        // Если все проверки пройдены
        return "APPROVED";
    }


    private boolean hasRepeatingCharacters(String content) {
        if (content.length() < 5) return false;

        for (int i = 0; i < content.length() - 5; i++) {
            char current = content.charAt(i);
            boolean allSame = true;

            for (int j = 1; j < 5; j++) {
                if (content.charAt(i + j) != current) {
                    allSame = false;
                    break;
                }
            }

            if (allSame) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MessageResponseTo createMessage(String country, Long topicId, MessageRequestTo request) {
        // Используем ID из запроса, если он есть
        Long messageId = request.getId();
        if (messageId == null) {
            messageId = idGenerator.getNextId();
        }

        // Определяем состояние
        String state = request.getState();
        if (state == null || state.isEmpty()) {
            state = moderateMessage(request.getContent());
        }

        Message message = messageMapper.toEntity(request, getCountryOrDefault(country), messageId);
        message.setState(state);

        Message savedMessage = messageRepository.save(message);
        MessageResponseTo response = messageMapper.toResponse(savedMessage);
        response.setAllBooleanFields();
        return response;
    }

    @Override
    public List<MessageResponseTo> getAllMessages() {
        List<Message> messages = messageRepository.findAllMessages();
        return messages.stream()
                .map(message -> {
                    MessageResponseTo response = messageMapper.toResponse(message);
                    response.setAllBooleanFields();
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseTo> getMessagesByTopic(String country, Long topicId) {
        List<Message> messages = messageRepository.findByCountryAndTopicId(
                getCountryOrDefault(country),
                topicId
        );
        return messages.stream()
                .map(message -> {
                    MessageResponseTo response = messageMapper.toResponse(message);
                    response.setAllBooleanFields();
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Slice<MessageResponseTo> getMessagesByTopic(String country, Long topicId, Pageable pageable) {
        Slice<Message> messages = messageRepository.findByCountryAndTopicId(
                getCountryOrDefault(country),
                topicId,
                pageable
        );
        return messages.map(message -> {
            MessageResponseTo response = messageMapper.toResponse(message);
            response.setAllBooleanFields();
            return response;
        });
    }

    @Override
    public MessageResponseTo getMessage(String country, Long topicId, Long messageId) {
        Message message = messageRepository.findByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );

        if (message == null) {
            throw new ResourceNotFoundException(
                    String.format("Message not found with country=%s, topicId=%d, id=%d",
                            country, topicId, messageId)
            );
        }

        MessageResponseTo response = messageMapper.toResponse(message);
        response.setAllBooleanFields();
        return response;
    }

    @Override
    public MessageResponseTo updateMessage(String country, Long topicId, Long messageId, MessageRequestTo request) {
        Message existingMessage = messageRepository.findByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );

        if (existingMessage == null) {
            throw new ResourceNotFoundException(
                    String.format("Message not found with country=%s, topicId=%d, id=%d",
                            country, topicId, messageId)
            );
        }

        // Обновляем только content, остальные поля нельзя менять после модерации
        existingMessage.setContent(request.getContent());
        existingMessage.setModified(LocalDateTime.now());

        // Если сообщение было отклонено, сбрасываем статус на PENDING
        if ("DECLINED".equals(existingMessage.getState()) || "DECLINE".equals(existingMessage.getState())) {
            existingMessage.setState("PENDING");
        }

        Message updatedMessage = messageRepository.save(existingMessage);
        MessageResponseTo response = messageMapper.toResponse(updatedMessage);
        response.setAllBooleanFields();
        return response;
    }

    @Override
    public void deleteMessage(String country, Long topicId, Long messageId) {
        if (!existsMessage(country, topicId, messageId)) {
            throw new ResourceNotFoundException(
                    String.format("Message not found with country=%s, topicId=%d, id=%d",
                            country, topicId, messageId)
            );
        }

        messageRepository.deleteByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );
    }

    @Override
    public boolean existsMessage(String country, Long topicId, Long messageId) {
        return messageRepository.existsByCountryAndTopicIdAndId(
                getCountryOrDefault(country),
                topicId,
                messageId
        );
    }

    @Override
    public Optional<MessageResponseTo> getMessageById(Long id) {
        try {
            // Пытаемся найти сообщение любым способом
            List<Message> messages = messageRepository.findByIdAllowFiltering(id);
            if (!messages.isEmpty()) {
                Message message = messages.get(0);
                MessageResponseTo response = messageMapper.toResponse(message);
                response.setAllBooleanFields();
                return Optional.of(response);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding message by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    private String getCountryOrDefault(String country) {
        return (country == null || country.trim().isEmpty()) ? DEFAULT_COUNTRY : country.trim();
    }
}

