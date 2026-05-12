package org.rv.lab1.controller.v2;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.CommentRequestTo;
import org.rv.lab1.dto.CommentResponseTo;
import org.rv.lab1.exception.ApiException;
import org.rv.lab1.security.V2AccessControl;
import org.rv.lab1.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V2 + "/comments")
public class CommentV2Controller {

    private final CommentService service;
    private final V2AccessControl access;

    public CommentV2Controller(CommentService service, V2AccessControl access) {
        this.service = service;
        this.access = access;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo create(@Valid @RequestBody CommentRequestTo request) {
        access.requireCommentWriteForStory(request.storyId());
        return service.create(request);
    }

    @GetMapping
    public List<CommentResponseTo> getAll() {
        access.requireUser();
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CommentResponseTo getById(@PathVariable long id) {
        access.requireUser();
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable long id, @Valid @RequestBody CommentRequestTo request) {
        var existing = service.getById(id);
        if (existing.storyId() != request.storyId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 6, "storyId mismatch for comment update");
        }
        access.requireCommentMutate(id);
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        access.requireCommentMutate(id);
        service.delete(id);
    }
}
