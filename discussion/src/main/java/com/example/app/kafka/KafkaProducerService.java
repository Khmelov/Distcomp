package com.example.app.kafka;

import com.example.app.dto.ReactionResponseDTO;
import com.example.app.dto.kafka.KafkaReactionMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topics.out-topic}")
    private String outTopic;
    
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    // Отправка результата модерации
    public void sendModerationResult(ReactionResponseDTO reaction) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            reaction.getId(),
            reaction.getTweetId(),
            reaction.getContent(),
            reaction.getCountry(),
            reaction.getState(),
            reaction.getCreatedAt(),
            reaction.getUpdatedAt(),
            "MODERATE"
        );
        
        // Ключ = tweetId, чтобы все сообщения для одного твита шли в одну партицию
        String key = String.valueOf(reaction.getTweetId());
        kafkaTemplate.send(outTopic, key, message);
        
        System.out.println("Sent moderation result to Kafka: " + message);
    }
    
    // Отправка информации о создании реакции
    public void sendReactionCreated(ReactionResponseDTO reaction) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            reaction.getId(),
            reaction.getTweetId(),
            reaction.getContent(),
            reaction.getCountry(),
            reaction.getState(),
            reaction.getCreatedAt(),
            reaction.getUpdatedAt(),
            "CREATE"
        );
        
        String key = String.valueOf(reaction.getTweetId());
        kafkaTemplate.send(outTopic, key, message);
    }
    
    // Отправка информации об обновлении реакции
    public void sendReactionUpdated(ReactionResponseDTO reaction) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            reaction.getId(),
            reaction.getTweetId(),
            reaction.getContent(),
            reaction.getCountry(),
            reaction.getState(),
            reaction.getCreatedAt(),
            reaction.getUpdatedAt(),
            "UPDATE"
        );
        
        String key = String.valueOf(reaction.getTweetId());
        kafkaTemplate.send(outTopic, key, message);
    }
    
    // Отправка информации об удалении реакции
    public void sendReactionDeleted(ReactionResponseDTO reaction) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            reaction.getId(),
            reaction.getTweetId(),
            reaction.getContent(),
            reaction.getCountry(),
            reaction.getState(),
            reaction.getCreatedAt(),
            reaction.getUpdatedAt(),
            "DELETE"
        );
        
        String key = String.valueOf(reaction.getTweetId());
        kafkaTemplate.send(outTopic, key, message);
    }
}