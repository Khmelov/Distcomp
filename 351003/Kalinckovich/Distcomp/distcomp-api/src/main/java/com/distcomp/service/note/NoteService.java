package com.distcomp.service.note;

import com.distcomp.data.repository.note.NoteReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.mapper.note.NoteMapper;
import com.distcomp.model.note.Note;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.note.NoteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        return noteRepository.findByTopicId(topicId, PageRequest.of(page, size))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> findById(final Long id) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.findById(id))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> update(final Long id, final NoteUpdateRequest request) {
        return noteValidator.validateUpdate(request, ValidationArgs.withId(id))
                .flatMap(validationResult -> noteRepository.findById(id))
                .flatMap(existing -> {
                    final Note updated = noteMapper.updateFromDto(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> patch(final Long id, final NotePatchRequest request) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.findById(id))
                .flatMap(existing -> {
                    final Note updated = noteMapper.updateFromPatch(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return noteValidator.validateNoteExists(id)
                .then(noteRepository.deleteById(id));
    }

    public Flux<NoteResponseDto> findAll(final int page, final int size) {
        final Pageable pageable = PageRequest.of(page, size);
        return noteRepository.findAllBy(pageable)
                .map(noteMapper::toResponse);
    }
}