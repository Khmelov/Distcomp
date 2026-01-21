package com.example.app.kafka;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.kafka.KafkaReactionMessage;
import com.example.app.service.ReactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    
    private final ReactionService reactionService;
    
    public KafkaConsumerService(ReactionService reactionService) {
        this.reactionService = reactionService;
    }
    
    @KafkaListener(
        topics = "${kafka.topics.in-topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeReactionMessage(KafkaReactionMessage message) {
        System.out.println("Received message from Kafka: " + message);
        
        try {
            ReactionRequestDTO request = new ReactionRequestDTO();
            request.setTweetId(message.getTweetId());
            request.setContent(message.getContent());
            request.setCountry(message.getCountry());
            request.setState(message.getState());
            
            switch (message.getOperation()) {
                case "CREATE":
                    // Для новых реакций ID будет сгенерирован в сервисе
                    reactionService.createReactionFromKafka(request);
                    break;
                    
                case "UPDATE":
                    // Для обновлений используем существующий ID
                    if (message.getId() != null) {
                        reactionService.updateReaction(
                            message.getCountry(),
                            message.getTweetId(),
                            message.getId(),
                            request
                        );
                    }
                    break;
                    
                case "DELETE":
                    if (message.getId() != null) {
                        reactionService.deleteReaction(
                            message.getCountry(),
                            message.getTweetId(),
                            message.getId()
                        );
                    }
                    break;
                    
                default:
                    System.err.println("Unknown operation: " + message.getOperation());
            }
            
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}