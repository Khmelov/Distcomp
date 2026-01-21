package com.example.app.kafka;

import com.example.app.dto.ReactionResponseDTO;
import com.example.app.dto.kafka.KafkaReactionMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    
    @KafkaListener(
        topics = "${kafka.topics.out-topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeModerationResult(KafkaReactionMessage message) {
        System.out.println("Received moderation result from Kafka: " + message);
        
        // Здесь можно:
        // 1. Обновить кэш реакций
        // 2. Уведомить пользователей об изменении состояния
        // 3. Записать в лог или БД для аудита
        
        // Пример: уведомление о модерации
        if ("MODERATE".equals(message.getOperation())) {
            System.out.println("Reaction " + message.getId() + 
                             " for tweet " + message.getTweetId() + 
                             " has been " + message.getState());
            
            // Можно отправить уведомление через WebSocket или другой механизм
            sendNotification(message);
        }
    }
    
    private void sendNotification(KafkaReactionMessage message) {
        // Реализация уведомлений
        System.out.println("Notification: Reaction " + message.getId() + 
                          " is now " + message.getState());
    }
}