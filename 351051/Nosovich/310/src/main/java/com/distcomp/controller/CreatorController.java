package com.distcomp.controller;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;
import com.distcomp.service.CreatorService;
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
 * REST resource of the authors.
 *
 * <p>Collection and item operations follow the uniform contract of the API:
 * {@code 201} with a {@code Location} header on creation, {@code 200} on reads
 * and updates, {@code 204} on deletion.</p>
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/creators")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @PostMapping
    public ResponseEntity<CreatorResponseTo> create(@RequestBody CreatorRequestTo request) {
        CreatorResponseTo created = creatorService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<CreatorResponseTo> findAll() {
        return creatorService.findAll();
    }

    @GetMapping("/{id}")
    public CreatorResponseTo findById(@PathVariable Long id) {
        return creatorService.findById(id);
    }

    /** Collection-level update: the identifier travels inside the payload. */
    @PutMapping
    public CreatorResponseTo update(@RequestBody CreatorRequestTo request) {
        return creatorService.update(request.id(), request);
    }

    /** Item-level update, kept as an alias of the collection-level operation. */
    @PutMapping("/{id}")
    public CreatorResponseTo update(@PathVariable Long id, @RequestBody CreatorRequestTo request) {
        return creatorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        creatorService.delete(id);
    }

    /** Author of the given issue. */
    @GetMapping("/by-issue/{issueId}")
    public CreatorResponseTo findByIssueId(@PathVariable Long issueId) {
        return creatorService.findByIssueId(issueId);
    }
}
