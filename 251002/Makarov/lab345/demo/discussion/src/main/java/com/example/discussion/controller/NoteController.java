package com.example.discussion.controller;

import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.dto.NoteResponseTo;
import com.example.discussion.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {


    private final NoteService noteService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String REQUEST_TOPIC = "InTopic";
    private static final String REPLY_TOPIC = "OutTopic";

    public NoteController(NoteService noteService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.noteService = noteService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = REQUEST_TOPIC,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}" // Используем group-id из конфига
    )
    public void handleKafkaRequest(
            @Payload NoteRequestTo request,
            @Header(KafkaHeaders.CORRELATION_ID) String correlationId,
            @Header("operation") String operation,
            @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic,
            Acknowledgment acknowledgment) { // Добавляем параметр для ручного подтверждения

        try {

            Object response;
            switch (operation) {
                case "getAll":
                    response = noteService.findAll();
                    break;
                case "getById":
                    response = noteService.findById(request.getId());
                    break;
                case "create":
                    response = noteService.save(request);
                    break;
                case "update":
                    response = noteService.update(request);
                    break;
                case "delete":
                    response = noteService.deleteById(request.getId());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + operation);
            }


            if (replyTopic != null) {
                sendReply(replyTopic, correlationId, response);
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            if (replyTopic != null) {
                sendErrorReply(replyTopic, correlationId, e);
            }
        }
    }

    private void sendReply(String replyTopic, String correlationId, Object response) {
        Message<Object> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, replyTopic)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader(KafkaHeaders.KEY, correlationId)
                .build();

        kafkaTemplate.send(message);
    }

    private void sendErrorReply(String replyTopic, String correlationId, Exception e) {
        Message<String> message = MessageBuilder
                .withPayload("Error: " + e.getMessage())
                .setHeader(KafkaHeaders.TOPIC, replyTopic)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader(KafkaHeaders.KEY, correlationId)
                .setHeader("error", true)
                .build();

        kafkaTemplate.send(message);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseTo>> getAllNotes() {
        List<NoteResponseTo> notes = noteService.findAll();
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        NoteResponseTo note = noteService.findById(id);
        return note != null
                ? new ResponseEntity<>(note, HttpStatus.OK)
                : new ResponseEntity<>(new Error("Note not found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<NoteResponseTo> createNote(@RequestBody @Valid NoteRequestTo noteRequestTo) {
        NoteResponseTo createdNote = noteService.save(noteRequestTo);
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<NoteResponseTo> updateNote(@RequestBody @Valid NoteRequestTo noteRequestTo) {
        NoteResponseTo updatedNote = noteService.update(noteRequestTo);
        return updatedNote != null
                ? new ResponseEntity<>(updatedNote, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}