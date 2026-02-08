package com.distcomp.service.note;

import com.distcomp.data.repository.note.NoteReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NotePatchRequest;
import com.distcomp.dto.note.NoteResponseDto;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.dto.user.UserResponseDto;
import com.distcomp.mapper.note.NoteMapper;
import com.distcomp.model.note.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteReactiveRepository noteRepository;
    private final NoteMapper noteMapper;

    public Mono<NoteResponseDto> create(final NoteCreateRequest request) {
        return noteRepository.save(noteMapper.toEntity(request))
                .map(noteMapper::toResponse);
    }

    public Flux<NoteResponseDto> findAllByTopicId(final Long topicId, final int page, final int size) {
        return noteRepository.findByTopicId(topicId, PageRequest.of(page, size))
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> findById(final Long id) {
        return noteRepository.findById(id)
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> update(final Long id, final NoteUpdateRequest request) {
        return noteRepository.findById(id)
                .flatMap((final Note existing) -> {
                    final Note updated = noteMapper.updateFromDto(request, existing);
                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<NoteResponseDto> patch(final Long id, final NotePatchRequest request) {
        return noteRepository.findById(id)
                .flatMap((final Note existing) -> {

                    final Note updated = noteMapper.updateFromPatch(request, existing);

                    return noteRepository.save(updated);
                })
                .map(noteMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return noteRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Note not found with id: " + id
                        ));
                    }
                    return noteRepository.deleteById(id);
                });
    }

    public Flux<NoteResponseDto> findAll(final int page, final int size) {
        final Pageable pageable = PageRequest.of(page, size);
        return noteRepository.findAllBy(pageable)
                .map(noteMapper::toResponse);
    }
}