package com.distcomp.service.topic;

import com.distcomp.data.repository.m2m.TopicTagReactiveRepository;
import com.distcomp.data.repository.note.NoteReactiveRepository;
import com.distcomp.data.repository.tag.TagReactiveRepository;
import com.distcomp.data.repository.topic.TopicReactiveRepository;
import com.distcomp.dto.topic.TopicCreateRequest;
import com.distcomp.dto.topic.TopicPatchRequest;
import com.distcomp.dto.topic.TopicResponseDto;
import com.distcomp.dto.topic.TopicUpdateRequest;
import com.distcomp.mapper.topic.TopicMapper;
import com.distcomp.model.m2m.TopicTag;
import com.distcomp.model.topic.Topic;
import com.distcomp.service.tag.TagService;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.topic.TopicValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicReactiveRepository topicRepository;
    private final TopicTagReactiveRepository topicTagRepository;
    private final TagReactiveRepository tagRepository;
    private final NoteReactiveRepository noteRepository;
    private final TopicMapper topicMapper;
    private final TopicValidator topicValidator;
    private final TagService tagService;

    public Mono<TopicResponseDto> create(final TopicCreateRequest request) {
        return topicValidator.validateCreate(request, ValidationArgs.empty())
                .flatMap(validationResult -> {
                    final Topic entity = topicMapper.toEntity(request);
                    return topicRepository.save(entity)
                            .flatMap(savedTopic -> {
                                final List<String> tagNames = request.getTags();
                                if (tagNames == null || tagNames.isEmpty()) {
                                    return Mono.just(savedTopic);
                                }
                                return Flux.fromIterable(tagNames)
                                        .flatMap(tagName -> tagService.findOrCreateByName(tagName))
                                        .collectList()
                                        .flatMap(tags -> {
                                            final List<TopicTag> links = tags.stream()
                                                    .map(tag -> new TopicTag(savedTopic.getId(), tag.getId()))
                                                    .collect(Collectors.toList());
                                            return topicTagRepository.saveAll(links)
                                                    .then(Mono.just(savedTopic));
                                        });
                            });
                })
                .map(topicMapper::toResponse);
    }

    public Flux<TopicResponseDto> findAll(final int page, final int size) {
        return topicRepository.findAllBy(PageRequest.of(page, size))
                .map(topicMapper::toResponse);
    }

    public Mono<TopicResponseDto> findById(final Long id) {
        return topicValidator.validateTopicExists(id)
                .then(topicRepository.findById(id))
                .map(topicMapper::toResponse);
    }

    public Mono<TopicResponseDto> update(final Long id, final TopicUpdateRequest request) {
        return topicValidator.validateUpdate(request, ValidationArgs.withId(id))
                .flatMap(validationResult -> topicRepository.findById(id))
                .flatMap(existing -> {
                    final Topic updated = topicMapper.updateFromDto(request, existing);
                    return topicRepository.save(updated);
                })
                .map(topicMapper::toResponse);
    }

    public Mono<TopicResponseDto> patch(final Long id, final TopicPatchRequest request) {
        return topicValidator.validateTopicExists(id)
                .then(topicRepository.findById(id))
                .flatMap(existing -> {
                    final Topic updated = topicMapper.updateFromPatch(request, existing);
                    return topicRepository.save(updated);
                })
                .map(topicMapper::toResponse);
    }

    public Mono<Void> delete(final Long id) {
        return topicValidator.validateTopicExists(id)
                .then(noteRepository.deleteByTopicId(id))
                .then(topicTagRepository.findByTopicId(id).collectList())
                .flatMap(topicTags -> {
                    final List<Long> tagIds = topicTags.stream()
                            .map(TopicTag::getTagId)
                            .collect(Collectors.toList());
                    return topicTagRepository.deleteByTopicId(id)
                            .thenMany(Flux.fromIterable(tagIds))
                            .flatMap(tagService::deleteTagIfUnused)
                            .then();
                })
                .then(topicRepository.deleteById(id));
    }

    public Flux<TopicResponseDto> findByUserId(final Long userId, final int page, final int size) {
        return topicRepository.findByUserWhoPostTopicId(userId, PageRequest.of(page, size))
                .map(topicMapper::toResponse);
    }
}