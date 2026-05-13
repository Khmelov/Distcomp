package by.distcomp.app.kafka;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import by.distcomp.app.service.NoteService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoteKafkaConsumer {

    private final NoteService noteService;
    private final KafkaTemplate<String, NoteResponseTo> kafkaTemplate;

    public NoteKafkaConsumer(NoteService noteService, KafkaTemplate<String, NoteResponseTo> kafkaTemplate) {
        this.noteService = noteService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void handleKafkaNote(NoteRequestTo request) {
        NoteResponseTo response = noteService.createNote(request);
        kafkaTemplate.send("OutTopic", response.id().toString(), response);
    }
}