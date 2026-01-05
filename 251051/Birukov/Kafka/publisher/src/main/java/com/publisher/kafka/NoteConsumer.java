package com.publisher.kafka;

import com.publisher.dto.request.NoteRequestTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoteConsumer {
	private static final Logger log = LoggerFactory.getLogger(NoteConsumer.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
	private final Map<String, NoteRequestTo> moderationResults = new ConcurrentHashMap<>();
    
	@KafkaListener(
        topics = "${kafka.topics.out-topic:OutTopic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeModerationResult(String message, Acknowledgment ack) {
        try {
            NoteRequestTo noteRequest = objectMapper.readValue(message, NoteRequestTo.class);
            String noteId = getNoteId(noteRequest);
            
			moderationResults.put(noteId, noteRequest);
            
			ack.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing note: ", e);
        }
    }
    
	public NoteRequestTo getModerationResult(String id) {
        String noteId = String.format("%s", id);
        return moderationResults.get(noteId);
    }
    
    private String getNoteId(NoteRequestTo note) {
        return String.format("%s", note.getId());
    }
}