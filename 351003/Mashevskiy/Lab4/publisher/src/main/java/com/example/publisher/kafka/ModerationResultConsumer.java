package com.example.publisher.kafka;

import com.example.publisher.dto.NoteMessage;
import com.example.publisher.entity.Note;
import com.example.publisher.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ModerationResultConsumer {

    @Autowired
    private NoteRepository noteRepository;

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void consume(NoteMessage message) {
        System.out.println("Received from OutTopic: Note " + message.getNoteId() + " -> " + message.getState());

        Note note = noteRepository.findById(message.getNoteId()).orElse(null);
        if (note != null) {
            note.setState(message.getState());
            noteRepository.save(note);
            System.out.println("Updated note " + note.getId() + " to " + note.getState());
        }
    }
}