package com.publisher.kafka;

import com.publisher.dto.request.NoteRequestTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoteProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
	
	NoteProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
    
    @Value("${kafka.topics.in-topic:InTopic}")
    private String inTopic;
    
	public CompletableFuture<SendResult<String, String>> sendNoteForModeration(NoteRequestTo noteRequest) {
        try {
            String noteJson = objectMapper.writeValueAsString(noteRequest);
            
			String key = String.valueOf(noteRequest.getTweetId());
            
			return kafkaTemplate.send(inTopic, key, noteJson);
            
        } catch (Exception e) {
			return CompletableFuture.failedFuture(e);
        }
    }
}