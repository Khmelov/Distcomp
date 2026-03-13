package com.distcomp.service.tag;

import com.distcomp.data.repository.tag.TagReactiveRepository;
import com.distcomp.dto.tag.TagCreateRequest;
import com.distcomp.dto.tag.TagPatchRequest;
import com.distcomp.dto.tag.TagResponseDto;
import com.distcomp.dto.tag.TagUpdateRequest;
import com.distcomp.mapper.tag.TagMapper;
import com.distcomp.model.tag.Tag;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.tag.TagValidator;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagReactiveRepository tagRepository;
    private final TagMapper tagMapper;
    private final TagValidator tagValidator;

    public Mono<TagResponseDto> create(final TagCreateRequest request) {
        return tagValidator.validateCreate(request, ValidationArgs.empty())
                .flatMap(validationResult -> {
                    final Tag entity = tagMapper.toEntity(request);
                    return tagRepository.save(entity);
                })
                .map(tagMapper::toResponse);
    }

    public Flux<TagResponseDto> findAll(final int page, final int size) {
        return tagRepository.findAllBy(PageRequest.of(page, size))
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> findById(final Long id) {
        return tagValidator.validateTagExists(id)
                .then(tagRepository.findById(id))
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> findByName(final String name) {
        return tagRepository.findByName(name)
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> update(final Long id, final TagUpdateRequest request) {
        return tagValidator.validateUpdate(request, ValidationArgs.withId(id))
                .flatMap(validationResult -> tagRepository.findById(id))
                .flatMap(existing -> {
                    final Tag updated = tagMapper.updateFromDto(request, existing);
                    return tagRepository.save(updated);
                })
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> patch(final Long id, final TagPatchRequest request) {
        return tagValidator.validateTagExists(id)
                .then(tagRepository.findById(id))
                .flatMap(existing -> {
                    final Tag updated = tagMapper.updateFromPatch(request, existing);
                    return tagRepository.save(updated);
                })
                .map(tagMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return tagValidator.validateTagExists(id)
                .then(tagRepository.deleteById(id));
    }

    public Mono<Void> deleteTagIfUnused(final Long tagId) {
        return tagRepository.deleteById(tagId)
                .onErrorResume(DataIntegrityViolationException.class,
                        _ -> Mono.empty());
    }

    public Mono<Tag> findOrCreateByName(final String name) {
        return tagRepository.findByName(name)
                .switchIfEmpty(tagRepository.save(new Tag(null, name)));
    }
}