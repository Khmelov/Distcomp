package com.distcomp.controller;

import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.dto.response.TagResponseTo;
import com.distcomp.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST resource of the tags.
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> create(@RequestBody TagRequestTo request) {
        TagResponseTo created = tagService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<TagResponseTo> findAll() {
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public TagResponseTo findById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    /** Collection-level update: the identifier travels inside the payload. */
    @PutMapping
    public TagResponseTo update(@RequestBody TagRequestTo request) {
        return tagService.update(request.id(), request);
    }

    /** Item-level update, kept as an alias of the collection-level operation. */
    @PutMapping("/{id}")
    public TagResponseTo update(@PathVariable Long id, @RequestBody TagRequestTo request) {
        return tagService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tagService.delete(id);
    }

    /** Tags attached to the given issue. */
    @GetMapping("/by-issue/{issueId}")
    public List<TagResponseTo> findByIssueId(@PathVariable Long issueId) {
        return tagService.findByIssueId(issueId);
    }
}
