package com.distcomp.service.tag;

import com.distcomp.data.repository.tag.TagReactiveRepository;
import com.distcomp.dto.tag.TagCreateRequest;
import com.distcomp.dto.tag.TagPatchRequest;
import com.distcomp.dto.tag.TagResponseDto;
import com.distcomp.dto.tag.TagUpdateRequest;
import com.distcomp.mapper.tag.TagMapper;
import com.distcomp.model.tag.Tag;
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
public class TagService {
    private final TagReactiveRepository tagRepository;
    private final TagMapper tagMapper;

    public Mono<TagResponseDto> create(final TagCreateRequest request) {
        return tagRepository.save(tagMapper.toEntity(request))
                .map(tagMapper::toResponse);
    }

    public Flux<TagResponseDto> findAll(final int page, final int size) {
        return tagRepository.findAllBy(PageRequest.of(page, size))
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> findById(final Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> findByName(final String name) {
        return tagRepository.findByName(name)
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> update(final Long id, final TagUpdateRequest request) {
        return tagRepository.findById(id)
                .flatMap((final Tag existing) -> {
                    final Tag updated = tagMapper.updateFromDto(request, existing);

                    return tagRepository.save(updated);
                })
                .map(tagMapper::toResponse);
    }

    public Mono<TagResponseDto> patch(final Long id, final TagPatchRequest request) {
        return tagRepository.findById(id)
                .flatMap((final Tag existing) -> {

                    final Tag updated = tagMapper.updateFromPatch(request, existing);

                    return tagRepository.save(updated);
                })
                .map(tagMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return tagRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Note not found with id: " + id
                        ));
                    }
                    return tagRepository.deleteById(id);
                });
    }
}