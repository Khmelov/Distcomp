package org.discussion.service;

import com.common.NoteAsyncResponse;
import com.common.NoteMessage;
import com.common.NoteResponseTo;
import com.common.NoteState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.discussion.dto.request.NoteRequestToDto;
import org.discussion.exception.NotFoundException;
import org.discussion.model.Note;
import org.discussion.model.NoteKey;
import org.discussion.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteService {

    private static final String COUNTRY = "US";

    CompositeIdCodec compositeIdCodec;
    NoteRepository repository;

    public NoteMessage create(NoteMessage message) {
        if (moderate(message.getContent())) {
            message.setState(NoteState.DECLINE);
            return message;
        }

        var key = compositeIdCodec.decode(message.getId());
        var note = new Note(key, message.getContent());

        repository.save(note);

        message.setState(NoteState.APPROVE);
        return message;
    }

    public NoteResponseTo create(NoteRequestToDto req) {
        validate(req.content());
        long newId = compositeIdCodec.encode(COUNTRY, req.issueId(), generateId());
        NoteKey key = compositeIdCodec.decode(newId);
        Note note = new Note(key, req.content());
        repository.save(note);
        return toDto(note);
    }

    public NoteAsyncResponse getAll(String correlationId) {
        var list = repository.findAll().stream()
                .toList();

        return toAsyncResponse(list, correlationId);
    }

    public List<NoteResponseTo> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }


    public NoteAsyncResponse getById(Long compositeId, String correlationId) {
        NoteKey key = compositeIdCodec.decode(compositeId);

        Note note = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        return toAsyncResponse(List.of(note), correlationId);
    }

    public NoteResponseTo getById(Long compositeId) {
        NoteKey key = compositeIdCodec.decode(compositeId);

        Note note = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        return toDto(note);
    }

    public List<NoteResponseTo> getByIssueId(Long issueId) {
        return repository.findAll().stream()
                .filter(n -> n.getKey().getIssueId().equals(issueId))
                .map(this::toDto)
                .toList();
    }

    public NoteAsyncResponse update(NoteMessage message) {
        validate(message.getContent());
        var key = compositeIdCodec.decode(message.getId());
        var note = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        note.setContent(message.getContent());
        repository.save(note);
        return toAsyncResponse(List.of(note), message.getCorrelationId());
    }

    public NoteResponseTo update(Long compositeId, NoteRequestToDto req) {
        validate(req.content());

        NoteKey key = compositeIdCodec.decode(compositeId);

        Note note = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        note.setContent(req.content());

        repository.save(note);

        return toDto(note);
    }

    public void delete(Long compositeId) {
        NoteKey key = compositeIdCodec.decode(compositeId);
        Note note = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Note not found"));
        repository.delete(note);
    }

    private boolean moderate(String content) {
        return Stream.of("bad", "spam", "hate", "offensive")
                .anyMatch(content::contains);
    }

    private void validate(String content) {
        if (content == null || content.length() < 2 || content.length() > 2048) {
            throw new RuntimeException("content must be 2..2048 chars");
        }
    }

    private NoteAsyncResponse toAsyncResponse(List<Note> notes, String correlationId) {
        return new NoteAsyncResponse(
                notes.stream()
                        .map(this::toDto)
                        .toList(),
                correlationId
        );
    }

    private NoteResponseTo toDto(Note note) {
        var key = compositeIdCodec.encode(
                note.getKey().getCountry(),
                note.getKey().getIssueId(),
                note.getKey().getId()
        );
        return new NoteResponseTo(
                key,
                note.getKey().getIssueId(),
                note.getContent(),
                NoteState.APPROVE
        );
    }

    private long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}