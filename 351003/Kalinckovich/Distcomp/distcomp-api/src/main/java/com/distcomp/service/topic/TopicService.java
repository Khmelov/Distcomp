package com.distcomp.service.topic;

import com.distcomp.data.repository.topic.TopicReactiveRepository;
import com.distcomp.dto.topic.TopicCreateRequest;
import com.distcomp.dto.topic.TopicPatchRequest;
import com.distcomp.dto.topic.TopicResponseDto;
import com.distcomp.dto.topic.TopicUpdateRequest;
import com.distcomp.mapper.topic.TopicMapper;
import com.distcomp.model.topic.Topic;
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
public class TopicService {
    private final TopicReactiveRepository topicRepository;
    private final TopicMapper topicMapper;

    public Mono<TopicResponseDto> create(final TopicCreateRequest request) {
        return topicRepository.save(topicMapper.toEntity(request))
                .map(topicMapper::toResponse);
    }

    public Flux<TopicResponseDto> findAll(final int page, final int size) {
        return topicRepository.findAllBy(PageRequest.of(page, size))
                .map(topicMapper::toResponse);
    }

    public Mono<TopicResponseDto> findById(final Long id) {
        return topicRepository.findById(id)
                .map(topicMapper::toResponse);
    }

    public Mono<TopicResponseDto> update(final Long id, final TopicUpdateRequest request) {
        return topicRepository.findById(id)
                .flatMap((final Topic existing) -> {
                    final Topic updated = topicMapper.updateFromDto(request, existing);
                    return topicRepository.save(updated);
                })
                .map(topicMapper::toResponse);
    }

    public Mono<TopicResponseDto> patch(final Long id, final TopicPatchRequest request) {
        return topicRepository.findById(id)
                .flatMap((final Topic existing) -> {
                    final Topic updated = topicMapper.updateFromPatch(request, existing);
                    return topicRepository.save(updated);
                })
                .map(topicMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return topicRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Note not found with id: " + id
                        ));
                    }
                    return topicRepository.deleteById(id);
                });
    }

    public Flux<TopicResponseDto> findByUserId(final Long userId, final int page, final int size) {
        return topicRepository.findByUserWhoPostTopicId(userId, PageRequest.of(page, size))
                .map(topicMapper::toResponse);
    }
}
