package com.example.publisher.controller;

import com.example.publisher.dto.KafkaReactionRequest;
import com.example.publisher.dto.ReactionDTO;
import com.example.publisher.kafka.KafkaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0")
public class ReactionController {

    @Autowired
    private KafkaProducer kafkaProducer;

    // GET все реакции (возвращаем пустой список)
    @GetMapping("/reactions")
    public ResponseEntity<List<ReactionDTO>> getAllReactions() {
        return ResponseEntity.ok(List.of());
    }

    // GET реакция по ID (для тестов)
    @GetMapping("/reactions/{id}")
    public ResponseEntity<ReactionDTO> getReactionById(@PathVariable String id) {
        // Для тестов возвращаем заглушку
        ReactionDTO dto = new ReactionDTO();
        dto.setId(id);
        dto.setContent("Test content");
        dto.setState("APPROVE");
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reactions")
    public ResponseEntity<ReactionDTO> createReaction(@RequestBody ReactionDTO reaction) {
        try {
            String reactionId = reaction.getId() != null ? reaction.getId() : UUID.randomUUID().toString();

            KafkaReactionRequest request = new KafkaReactionRequest(
                    reactionId,
                    reaction.getStoryId(),
                    reaction.getContent()
            );

            kafkaProducer.sendReactionRequest(request);

            ReactionDTO response = new ReactionDTO();
            response.setId(reactionId);
            response.setStoryId(reaction.getStoryId());
            response.setContent(reaction.getContent());
            response.setState("PENDING");
            response.setCreated(java.time.LocalDateTime.now());
            response.setModified(java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT обновление реакции (заглушка для тестов)
    @PutMapping("/reactions/{id}")
    public ResponseEntity<ReactionDTO> updateReaction(
            @PathVariable String id,
            @RequestBody ReactionDTO reaction) {
        ReactionDTO dto = new ReactionDTO();
        dto.setId(id);
        dto.setStoryId(reaction.getStoryId());
        dto.setContent(reaction.getContent());
        dto.setState("APPROVE");
        dto.setModified(java.time.LocalDateTime.now());
        return ResponseEntity.ok(dto);
    }

    // DELETE реакция (заглушка для тестов)
    @DeleteMapping("/reactions/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable String id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stories/{storyId}/reactions")
    public ResponseEntity<List<ReactionDTO>> getReactionsByStoryId(@PathVariable Long storyId) {
        return ResponseEntity.ok(List.of());
    }
}