package com.distcomp.service;

import com.distcomp.errorhandling.exceptions.NoteNotFoundException;
import com.distcomp.errorhandling.model.ValidationError;
import com.distcomp.repository.cassandra.NoteCassandraReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.mapper.note.NoteMapper;
import com.distcomp.model.note.Note;
import com.distcomp.validation.model.ValidationArgs;
import com.distcomp.validation.note.NoteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service("cassandraNoteService")
public class NoteService {

    private static final String DEFAULT_COUNTRY = "default";

    private final NoteCassandraReactiveRepository noteRepository;
    private final NoteMapper noteMapper;
    private final NoteValidator noteValidator;

    @Autowired
    public NoteService(final NoteCassandraReactiveRepository noteRepository,
                       final NoteMapper noteMapper,
                       final NoteValidator noteValidator) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.noteValidator = noteValidator;
    }

    public Mono<NoteResponseDto> findById(final Long topicId, final Long noteId) {
        return noteValidator.validateNoteExists(topicId, noteId)
                .then(noteRepository.findById(new Note.NoteKey(DEFAULT_COUNTRY, topicId, noteId)))
                .map(noteMapper::toResponse);
    }

    public Flux<NoteResponseDto> findAllByTopicId(final Long topicId, final int page, final int size) {
        return noteRepository.findByKeyCountryAndKeyTopicId(DEFAULT_COUNTRY, topicId, PageRequest.of(page, size))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> create(final NoteCreateRequest request) {
        return noteValidator.validateCreate(request, ValidationArgs.empty())
                .flatMap(validationResult -> {
                    final Long newId = IdGenerator.nextId();
                    final Note entity = noteMapper.toEntityWithKey(request, DEFAULT_COUNTRY, newId);
                    return noteRepository.save(entity);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> update(final Long topicId, final Long noteId, final NoteUpdateRequest request) {
        final ValidationArgs args = ValidationArgs.of(topicId, Map.of("noteId", noteId));
        return noteValidator.validateUpdate(request, args)
                .flatMap(_ -> noteRepository.findById(new Note.NoteKey(DEFAULT_COUNTRY, topicId, noteId)))
                .flatMap(existing -> {
                    final Note updated = noteMapper.updateFromDto(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> patch(final Long topicId, final Long noteId, final NotePatchRequest request) {
        return noteValidator.validateNoteExists(topicId, noteId)
                .then(noteRepository.findById(new Note.NoteKey(DEFAULT_COUNTRY, topicId, noteId)))
                .flatMap(existing -> {
                    final Note updated = noteMapper.updateFromPatch(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<Void> delete(final Long topicId, final Long noteId) {
        return noteValidator.validateNoteExists(topicId, noteId)
                .then(noteRepository.deleteById(new Note.NoteKey(DEFAULT_COUNTRY, topicId, noteId)));
    }

    

    public Mono<NoteResponseDto> findById(final Long id) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.findByNoteId(id))
                .map(noteMapper::toResponse);
    }

    public Flux<NoteResponseDto> findAll(final int page, final int size) {
        return noteRepository.findByKeyCountry(DEFAULT_COUNTRY, PageRequest.of(page, size))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> update(final Long id, final NoteUpdateRequest request) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.findByNoteId(id))
                .flatMap(existing -> {
                    
                    if (!existing.getTopicId().equals(request.getTopicId())) {
                        final List<ValidationError> errors = List.of(
                                new ValidationError("topicId", "Topic ID cannot be changed")
                        );
                        return Mono.error(new NoteNotFoundException(errors)); 
                    }
                    final Note updated = noteMapper.updateFromDto(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> patch(final Long id, final NotePatchRequest request) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.findByNoteId(id))
                .flatMap(existing -> {
                    
                    if (request.getContent() != null) {
                        existing.setContent(request.getContent());
                    }
                    
                    return noteRepository.save(existing);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.findByNoteId(id))
                .flatMap(noteRepository::delete);
    }

    public Mono<Void> deleteByTopicId(final Long topicId) {
        return noteRepository.deleteByCountryAndTopicId(DEFAULT_COUNTRY, topicId);
    }
}