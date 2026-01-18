package org.discussion.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.discussion.dto.request.NoteRequestToDto;
import org.discussion.dto.response.NoteResponseToDto;
import org.discussion.model.Note;
import org.discussion.model.NoteKey;
import org.discussion.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoteService {

    private static final String COUNTRY = "US";

    CompositeIdCodec compositeIdCodec;
    NoteRepository repository;

    public NoteResponseToDto create(NoteRequestToDto req) {
        validate(req);

        long newId = compositeIdCodec
                .encode(
                        COUNTRY,
                        req.issueId(),
                        generateId()
                );
        Note note = new Note(
                compositeIdCodec.decode(newId),
                req.content()
        );
        repository
                .save(note);
        return toDto(note);
    }

    public List<NoteResponseToDto> getAll() {
        return repository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public NoteResponseToDto getById(Long compositeId) {
        NoteKey key = compositeIdCodec
                .decode(compositeId);
        Note note = repository.findById(key)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return toDto(note);
    }

    public List<NoteResponseToDto> getByIssueId(Long issueId) {
        return repository
                .findAll()
                .stream()
                .filter(n -> n.getKey().getIssueId().equals(issueId))
                .map(this::toDto)
                .toList();
    }

    public NoteResponseToDto update(Long compositeId, NoteRequestToDto req) {
        validate(req);

        NoteKey key = compositeIdCodec.decode(compositeId);

        Note note = repository.findById(key)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setContent(req.content());

        repository.save(note);

        return toDto(note);
    }

    public void delete(Long compositeId) {
        NoteKey key = compositeIdCodec.decode(compositeId);
        repository.deleteById(key);
    }

    private void validate(NoteRequestToDto req) {
        if (req.content() == null || req.content().length() < 2 || req.content().length() > 2048) {
            throw new RuntimeException("content must be 2..2048 chars");
        }
    }

    private NoteResponseToDto toDto(Note note) {
        var note_key = note.getKey();
        var key = compositeIdCodec.encode(
                note_key.getCountry(),
                note_key.getIssueId(),
                note_key.getId()
        );
        return new NoteResponseToDto(
                note_key.getCountry(),
                note_key.getIssueId(),
                key,
                note.getContent()
        );
    }

    private long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}