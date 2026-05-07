package com.distcomp.controller.tag;

import com.distcomp.dto.tag.TagCreateRequest;
import com.distcomp.dto.tag.TagPatchRequest;
import com.distcomp.dto.tag.TagResponseDto;
import com.distcomp.dto.tag.TagUpdateRequest;
import com.distcomp.service.tag.TagService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<TagResponseDto> getAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return tagService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TagResponseDto> getById(@PathVariable final Long id) {
        return tagService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TagResponseDto> create(@Valid @RequestBody final TagCreateRequest request) {
        return tagService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TagResponseDto> update(@PathVariable final Long id, @Valid @RequestBody final TagUpdateRequest request) {
        return tagService.update(id, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TagResponseDto> patch(@PathVariable final Long id, @RequestBody final TagPatchRequest request) {
        return tagService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable final Long id) {
        return tagService.delete(id);
    }
}
