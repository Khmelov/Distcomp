package by.rest.publisher.controller;

import by.rest.publisher.dto.kafka.CommentKafkaRequest;
import by.rest.publisher.service.KafkaProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/test/kafka")
public class TestKafkaController {
    
    private final KafkaProducerService kafkaProducerService;
    
    public TestKafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }
    
    @PostMapping("/comment")
    public Map<String, String> sendTestComment(
            @RequestParam String text,
            @RequestParam Long storyId) {
        
        UUID commentId = UUID.randomUUID();
        CommentKafkaRequest request = new CommentKafkaRequest(
            commentId.toString(), 
            text, 
            storyId,
            "test-user",
            System.currentTimeMillis()
        );
        
        kafkaProducerService.sendCommentForModeration(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "sent");
        response.put("commentId", commentId.toString());
        response.put("storyId", storyId.toString());
        response.put("message", "Test comment sent for moderation");
        
        return response;
    }
    
    @PostMapping("/comment/approve")
    public Map<String, String> sendApproveComment(@RequestParam Long storyId) {
        String text = "Это отличная статья! Очень полезная информация для читателей.";
        UUID commentId = UUID.randomUUID();
        
        CommentKafkaRequest request = new CommentKafkaRequest(
            commentId.toString(), 
            text, 
            storyId,
            "test-user",
            System.currentTimeMillis()
        );
        
        kafkaProducerService.sendCommentForModeration(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "sent");
        response.put("commentId", commentId.toString());
        response.put("expectedStatus", "APPROVE");
        response.put("message", "APPROVE test comment sent (should pass moderation)");
        
        return response;
    }
    
    @PostMapping("/comment/decline")
    public Map<String, String> sendDeclineComment(@RequestParam Long storyId) {
        String text = "Купите viagra по низкой цене! Спешите, акция ограничена!";
        UUID commentId = UUID.randomUUID();
        
        CommentKafkaRequest request = new CommentKafkaRequest(
            commentId.toString(), 
            text, 
            storyId,
            "spammer",
            System.currentTimeMillis()
        );
        
        kafkaProducerService.sendCommentForModeration(request);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "sent");
        response.put("commentId", commentId.toString());
        response.put("expectedStatus", "DECLINE");
        response.put("message", "DECLINE test comment sent (should fail moderation)");
        
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Kafka Test Controller");
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }
}