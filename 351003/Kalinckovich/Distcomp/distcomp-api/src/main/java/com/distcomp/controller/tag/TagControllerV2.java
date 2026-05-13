package com.distcomp.controller.tag;

import com.distcomp.dto.tag.TagCreateRequest;
import com.distcomp.dto.tag.TagPatchRequest;
import com.distcomp.dto.tag.TagResponseDto;
import com.distcomp.dto.tag.TagUpdateRequest;
import com.distcomp.service.tag.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v2.0/tags")
@RequiredArgsConstructor
public class TagControllerV2 {

    private final TagService tagService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Flux<TagResponseDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return tagService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Mono<TagResponseDto> getById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<TagResponseDto> create(@Valid @RequestBody TagCreateRequest request) {
        return tagService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<TagResponseDto> update(@PathVariable Long id,
                                       @Valid @RequestBody TagUpdateRequest request) {
        return tagService.update(id, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<TagResponseDto> patch(@PathVariable Long id,
                                      @RequestBody TagPatchRequest request) {
        return tagService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> delete(@PathVariable Long id) {
        return tagService.delete(id);
    }
}