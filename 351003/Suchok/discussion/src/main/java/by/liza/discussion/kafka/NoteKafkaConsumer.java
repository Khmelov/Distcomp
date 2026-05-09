package by.liza.discussion.kafka;

import by.liza.discussion.model.Note;
import by.liza.discussion.repository.NoteRepository;
import by.liza.discussion.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Component @RequiredArgsConstructor
public class NoteKafkaConsumer {
    private final NoteRepository noteRepository;
    private final NoteKafkaProducer producer;
    private final ModerationService moderationService;

    @KafkaListener(topics = "${kafka.topic.in}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NoteKafkaMessage msg) {
        log.debug("Received from InTopic op={} reqId={}", msg.getOperation(), msg.getRequestId());
        try {
            switch (msg.getOperation()) {
                case CREATE   -> handleCreate(msg);
                case GET_BY_ID-> handleGetById(msg);
                case GET_ALL  -> handleGetAll(msg);
                case UPDATE   -> handleUpdate(msg);
                case DELETE   -> handleDelete(msg);
            }
        } catch (Exception e) {
            producer.sendError(msg.getRequestId(), msg.getArticleId(), 40404, e.getMessage());
        }
    }

    private void handleCreate(NoteKafkaMessage msg) {
        NoteKafkaDto dto = msg.getNote();
        String state = moderationService.moderate(dto.getContent());
        Note note = Note.builder()
                .id(dto.getId() != null ? dto.getId() : System.currentTimeMillis())
                .articleId(dto.getArticleId())
                .content(dto.getContent())
                .state(state)
                .build();
        noteRepository.save(note);
        producer.sendNote(msg.getRequestId(), msg.getArticleId(), toDto(note));
    }

    private void handleGetById(NoteKafkaMessage msg) {
        Optional<Note> found = noteRepository.findById(msg.getNoteId());
        if (found.isEmpty()) {
            producer.sendError(msg.getRequestId(), msg.getArticleId(), 40404,
                    "Note not found: " + msg.getNoteId());
            return;
        }
        producer.sendNote(msg.getRequestId(), msg.getArticleId(), toDto(found.get()));
    }

    private void handleGetAll(NoteKafkaMessage msg) {
        List<NoteKafkaDto> list = noteRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
        producer.sendNoteList(msg.getRequestId(), list);
    }

    private void handleUpdate(NoteKafkaMessage msg) {
        NoteKafkaDto dto = msg.getNote();
        Optional<Note> found = noteRepository.findById(dto.getId());
        if (found.isEmpty()) {
            producer.sendError(msg.getRequestId(), msg.getArticleId(), 40404,
                    "Note not found: " + dto.getId());
            return;
        }
        Note note = found.get();
        note.setContent(dto.getContent());
        note.setArticleId(dto.getArticleId());
        noteRepository.save(note);
        producer.sendNote(msg.getRequestId(), msg.getArticleId(), toDto(note));
    }

    private void handleDelete(NoteKafkaMessage msg) {
        Optional<Note> found = noteRepository.findById(msg.getNoteId());
        if (found.isEmpty()) {
            producer.sendError(msg.getRequestId(), msg.getArticleId(), 40404,
                    "Note not found: " + msg.getNoteId());
            return;
        }
        noteRepository.delete(found.get());
        producer.sendNote(msg.getRequestId(), msg.getArticleId(),
                NoteKafkaDto.builder().id(msg.getNoteId()).build());
    }

    private NoteKafkaDto toDto(Note n) {
        return NoteKafkaDto.builder()
                .id(n.getId()).articleId(n.getArticleId())
                .content(n.getContent()).state(n.getState()).build();
    }
}