package com.discussion.kafka;

import com.discussion.entity.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoteProducer {
    private static final Logger log = LoggerFactory.getLogger(NoteConsumer.class);
	
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
	
	NoteProducer(KafkaTemplate<String, String> kafkaTemplate,
				 ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}
    
    @Value("${kafka.topics.out-topic:OutTopic}")
    private String outTopic;
    
    public CompletableFuture<SendResult<String, String>> sendModerationResult(Note note) {
        try {
            if (note == null) {
                log.error("Attempted to send null note");
                return CompletableFuture.completedFuture(null);
            }
            
            Note.NoteKey key = note.getKey();
            if (key == null) {
                log.error("Note key is null for note: {}", note);
                return CompletableFuture.completedFuture(null);
            }
            
            String noteJson = objectMapper.writeValueAsString(note);
            String kafkaKey = generateKafkaKey(key);
            
            log.debug("Sending note {} to topic {}", key.getTweetId(), outTopic);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(outTopic, kafkaKey, noteJson);
            
			future.handle((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send note {}: ", key.getTweetId(), ex);
                } else if (result != null) {
                    log.info("Note {} sent successfully: partition={}, offset={}", 
                        key.getTweetId(), 
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
                return result;
            });
            
            return future;
            
        } catch (Exception e) {
            log.error("Error preparing note for sending: ", e);
            CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
    
    private String generateKafkaKey(Note.NoteKey key) {
		return String.valueOf(key.getTweetId());
    }
}