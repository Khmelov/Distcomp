package com.distcomp.controller;

import com.distcomp.dto.filter.CreatorFilter;
import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;
import com.distcomp.pagination.PageRequests;
import com.distcomp.pagination.PagedResult;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * REST resource of the authors.
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/creators")
public class CreatorController {

    private static final Set<String> SORTABLE = Set.of("id", "login", "firstname", "lastname");

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

    /**
     * @param page  zero-based page number, optional
     * @param size  page size, optional
     * @param sort  sort expression, defaults to {@code id,asc}
     * @param login optional login fragment
     */
    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String login) {

        PagedResult<CreatorResponseTo> result = creatorService.findAll(
                new CreatorFilter(login), PageRequests.of(page, size, sort, "id,asc", SORTABLE));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.total()))
                .body(result.content());
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
