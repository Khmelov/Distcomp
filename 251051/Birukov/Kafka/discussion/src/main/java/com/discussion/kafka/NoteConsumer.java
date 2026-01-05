package com.discussion.kafka;

import com.discussion.entity.Note;
import com.discussion.dto.request.NoteRequestTo;
import com.discussion.service.NoteService;
import com.discussion.kafka.NoteProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoteConsumer {
    private static final Logger log = LoggerFactory.getLogger(NoteConsumer.class);
	
    private final NoteService noteService;
    private final NoteProducer noteProducer;
    private final ObjectMapper objectMapper;
	
	NoteConsumer (NoteService noteService,
				  NoteProducer noteProducer,
				  ObjectMapper objectMapper) {
		this.noteService = noteService;
		this.noteProducer = noteProducer;
		this.objectMapper = objectMapper;
	}
    
    @KafkaListener(
        topics = "${kafka.topics.in-topic:InTopic}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNote(ConsumerRecord<String, String> record, Acknowledgment ack) {
        final String messageKey = record.key();
        final long offset = record.offset();
        final int partition = record.partition();
        
        log.info("Starting processing: key={}, partition={}, offset={}", 
            messageKey, partition, offset);
        
        try {
			Note note = deserializeNote(record.value());
            if (note == null || note.getKey() == null) {
                log.error("Invalid note received");
                ack.acknowledge();
                return;
            }
            
            Long noteId = note.getKey().getId();
            String country = note.getKey().getCountry();
            
            log.info("Processing note: id={}, country={}", noteId, country);
            
			Note moderatedNote = noteService.updateNote(noteId, noteService.NoteToRequest(note));
            
			noteProducer.sendModerationResult(moderatedNote);
            
			ack.acknowledge();
            
            log.info("Successfully processed note: id={}, newState={}", 
                noteId, moderatedNote.getState());
            
        } catch (JsonProcessingException e) {
            log.error("JSON parsing error for key={}: ", messageKey, e);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Unexpected error processing key={}: ", messageKey, e);
        }
    }
    
    private Note deserializeNote(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Note.class);
    }
}