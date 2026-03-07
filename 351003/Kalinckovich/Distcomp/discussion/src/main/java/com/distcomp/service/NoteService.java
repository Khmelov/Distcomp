package com.distcomp.service;

import com.distcomp.data.repository.note.NoteReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.mapper.note.NoteMapper;
import com.distcomp.model.note.Note;
import com.distcomp.validation.model.ValidationArgs;
import com.distcomp.validator.note.NoteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteReactiveRepository noteRepository;
    private final NoteMapper noteMapper;
    private final NoteValidator noteValidator;

    public Mono<NoteResponseDto> create(final NoteCreateRequest request) {
        return noteValidator.validateCreate(request, ValidationArgs.empty())
                .flatMap(validationResult -> {
                    final Note entity = noteMapper.toEntity(request);
                    return noteRepository.save(entity);
                })
                .map(noteMapper::toResponse);
    }

    public Flux<NoteResponseDto> findAllByTopicId(final Long topicId, final int page, final int size) {
        return noteRepository.findByKeyTopicId(topicId, PageRequest.of(page, size))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> findById(final Long topicId, final UUID noteId) {
        return noteValidator.validateNoteExists(topicId, noteId)
                .then(noteRepository.findById(new NoteKey(topicId, noteId)))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> update(final Long topicId, final UUID noteId, final NoteUpdateRequest request) {
        ValidationArgs args = ValidationArgs.of(topicId, Map.of("noteId", noteId));
        return noteValidator.validateUpdate(request, args)
                .flatMap(validationResult -> noteRepository.findById(new NoteKey(topicId, noteId)))
                .flatMap(existing -> {
                    final Note updated = noteMapper.updateFromDto(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> patch(final Long topicId, final UUID noteId, final NotePatchRequest request) {
        return noteValidator.validateNoteExists(topicId, noteId)
                .then(noteRepository.findById(new NoteKey(topicId, noteId)))
                .flatMap(existing -> {
                    final Note updated = noteMapper.updateFromPatch(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<Void> delete(final Long topicId, final UUID noteId) {
        return noteValidator.validateNoteExists(topicId, noteId)
                .then(noteRepository.deleteById(new NoteKey(topicId, noteId)));
    }

    // Global findAll – inefficient in Cassandra; consider removing or using token-based pagination.
    public Flux<NoteResponseDto> findAll(final int page, final int size) {
        return noteRepository.findAll(PageRequest.of(page, size))
                .map(noteMapper::toResponse);
    }
}