package by.rest.discussion.controller;

import by.rest.discussion.service.KafkaProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/test/kafka")
public class TestKafkaController {
    
    private final KafkaProducerService kafkaProducerService;
    
    public TestKafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }
    
    @PostMapping("/send-test")
    public Map<String, String> sendTestMessage() {
        kafkaProducerService.sendTestMessage();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "sent");
        response.put("message", "Test message sent from discussion module");
        response.put("timestamp", java.time.Instant.now().toString());
        
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Discussion Kafka Test Controller");
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }
}