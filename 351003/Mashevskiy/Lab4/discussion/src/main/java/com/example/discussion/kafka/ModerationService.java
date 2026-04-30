package com.example.discussion.kafka;

import com.example.discussion.controller.DiscussionNoteController;
import com.example.discussion.dto.NoteMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {

    @Autowired
    private KafkaTemplate<String, NoteMessage> kafkaTemplate;

    @Autowired
    private DiscussionNoteController noteController;

    private static final String[] BAD_WORDS = {"spam", "bad", "xxx", "virus", "hack", "phishing"};

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void moderate(NoteMessage message) {
        System.out.println("==================================================");
        System.out.println("DISCUSSION RECEIVED MESSAGE:");
        System.out.println("  Note ID: " + message.getNoteId());
        System.out.println("  State: " + message.getState());

        if ("DELETED".equals(message.getState())) {
            System.out.println("!!! PROCESSING DELETE for note " + message.getNoteId());
            noteController.deleteNote(message.getNoteId());
            System.out.println("Note " + message.getNoteId() + " DELETED");
            return;
        }

        System.out.println("  Text: " + message.getText());
        System.out.println("  Tweet ID: " + message.getTweetId());

        String text = message.getText().toLowerCase();
        boolean approved = true;

        for (String badWord : BAD_WORDS) {
            if (text.contains(badWord)) {
                approved = false;
                break;
            }
        }

        DiscussionNoteController.NoteInfo note = new DiscussionNoteController.NoteInfo();
        note.setId(message.getNoteId());
        note.setTweetId(message.getTweetId());
        note.setContent(message.getText());
        note.setState(approved ? "APPROVED" : "DECLINED");

        noteController.saveNote(note);
        System.out.println("Note " + message.getNoteId() + " SAVED with state: " + note.getState());

        message.setState(note.getState());
        kafkaTemplate.send("OutTopic", String.valueOf(message.getTweetId()), message);
        System.out.println("Result sent to OutTopic");
        System.out.println("==================================================");
    }
}