package com.example.app.kafka;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.dto.kafka.KafkaReactionMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaProducerService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topics.in-topic}")
    private String inTopic;
    
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    // Отправка реакции на модерацию
    public ReactionResponseDTO sendReactionForModeration(ReactionRequestDTO request) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            null, // ID будет сгенерирован в discussion модуле
            request.getTweetId(),
            request.getContent(),
            request.getCountry(),
            "PENDING",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "CREATE"
        );
        
        // Ключ = tweetId для гарантии порядка в одной партиции
        String key = String.valueOf(request.getTweetId());
        kafkaTemplate.send(inTopic, key, message);
        
        System.out.println("Sent reaction for moderation to Kafka: " + message);
        
        // Возвращаем временный ответ (в реальном приложении нужно ждать ответа из out-topic)
        return createTemporaryResponse(request);
    }
    
    // Отправка обновления реакции
    public ReactionResponseDTO sendReactionUpdate(Long reactionId, ReactionRequestDTO request) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            reactionId,
            request.getTweetId(),
            request.getContent(),
            request.getCountry(),
            "PENDING", // При обновлении снова требуется модерация
            LocalDateTime.now(),
            LocalDateTime.now(),
            "UPDATE"
        );
        
        String key = String.valueOf(request.getTweetId());
        kafkaTemplate.send(inTopic, key, message);
        
        System.out.println("Sent reaction update to Kafka: " + message);
        
        return createTemporaryResponse(request, reactionId);
    }
    
    // Отправка удаления реакции
    public void sendReactionDeletion(Long reactionId, Long tweetId) {
        KafkaReactionMessage message = new KafkaReactionMessage(
            reactionId,
            tweetId,
            null, // Контент не нужен для удаления
            "global",
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "DELETE"
        );
        
        String key = String.valueOf(tweetId);
        kafkaTemplate.send(inTopic, key, message);
        
        System.out.println("Sent reaction deletion to Kafka: " + message);
    }
    
    // Вспомогательный метод для создания временного ответа
    private ReactionResponseDTO createTemporaryResponse(ReactionRequestDTO request) {
        ReactionResponseDTO response = new ReactionResponseDTO();
        response.setTweetId(request.getTweetId());
        response.setContent(request.getContent());
        response.setCountry(request.getCountry());
        response.setState("PENDING");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }
    
    private ReactionResponseDTO createTemporaryResponse(ReactionRequestDTO request, Long id) {
        ReactionResponseDTO response = createTemporaryResponse(request);
        response.setId(id);
        return response;
    }
}