package com.distcomp.controller.topic;

import com.distcomp.dto.topic.TopicCreateRequest;
import com.distcomp.dto.topic.TopicPatchRequest;
import com.distcomp.dto.topic.TopicResponseDto;
import com.distcomp.dto.topic.TopicUpdateRequest;
import com.distcomp.service.topic.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<TopicResponseDto> getAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return topicService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TopicResponseDto> getById(@PathVariable final Long id) {
        return topicService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TopicResponseDto> create(@Valid @RequestBody final TopicCreateRequest request) {
        return topicService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TopicResponseDto> update(@PathVariable final Long id, @Valid @RequestBody final TopicUpdateRequest request) {
        return topicService.update(id, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TopicResponseDto> patch(@PathVariable final Long id, @RequestBody final TopicPatchRequest request) {
        return topicService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable final Long id) {
        return topicService.delete(id);
    }

    @GetMapping(params = "userId")
    @ResponseStatus(HttpStatus.OK)
    public Flux<TopicResponseDto> getByUserId(
            @RequestParam final Long userId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return topicService.findByUserId(userId, page, size);
    }
}
