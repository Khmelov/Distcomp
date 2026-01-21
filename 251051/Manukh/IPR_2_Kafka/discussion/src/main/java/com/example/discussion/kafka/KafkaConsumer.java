package com.example.discussion.kafka;

import com.example.discussion.entity.Reaction;
import com.example.discussion.repository.ReactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KafkaConsumer {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String IN_TOPIC = "InTopic";
    private static final String GROUP_ID = "discussion-group";

    // Список "стоп-слов" для модерации
    private static final String[] BAD_WORDS = {"спам", "оскорбление", "реклама", "scam"};

    @KafkaListener(topics = IN_TOPIC, groupId = GROUP_ID)
    public void consume(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);

            String id = json.get("id").asText();
            Long storyId = json.get("storyId").asLong();
            String content = json.get("content").asText();

            System.out.println("Processing reaction: id=" + id + ", storyId=" + storyId);

            // Модерация контента
            String state = moderateContent(content) ? "APPROVE" : "DECLINE";

            // Сохраняем в Cassandra
            Reaction reaction = new Reaction();
            reaction.setId(id);
            reaction.setStoryId(storyId);
            reaction.setContent(content);
            reaction.setState(state);
            reaction.setCreated(LocalDateTime.now());
            reaction.setModified(LocalDateTime.now());

            reactionRepository.save(reaction);

            // Отправляем ответ
            kafkaProducer.sendReactionResponse(id, state);

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

    private boolean moderateContent(String content) {
        String lowerContent = content.toLowerCase();

        // Проверка на стоп-слова
        for (String badWord : BAD_WORDS) {
            if (lowerContent.contains(badWord)) {
                return false; // DECLINE
            }
        }

        // Дополнительные проверки
        if (content.length() < 5) {
            return false; // Слишком короткий
        }

        return true; // APPROVE
    }
}