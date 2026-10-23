package com.distcomp.controller;

import com.distcomp.dto.filter.IssueFilter;
import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;
import com.distcomp.pagination.PageRequests;
import com.distcomp.pagination.PagedResult;
import com.distcomp.service.IssueService;
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
 * REST resource of the issues.
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/issues")
public class IssueController {

    private static final Set<String> SORTABLE = Set.of("id", "title", "created", "modified");

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponseTo> create(@RequestBody IssueRequestTo request) {
        IssueResponseTo created = issueService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * @param page      zero-based page number, optional
     * @param size      page size, optional
     * @param sort      sort expression, defaults to {@code id,asc}
     * @param creatorId optional author identifier
     * @param title     optional title fragment
     * @param tag       optional tag name the issue must carry
     */
    @GetMapping
    public ResponseEntity<List<IssueResponseTo>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag) {

        PagedResult<IssueResponseTo> result = issueService.findAll(
                new IssueFilter(creatorId, title, tag),
                PageRequests.of(page, size, sort, "id,asc", SORTABLE));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.total()))
                .body(result.content());
    }

    @GetMapping("/{id}")
    public IssueResponseTo findById(@PathVariable Long id) {
        return issueService.findById(id);
    }

    /** Collection-level update: the identifier travels inside the payload. */
    @PutMapping
    public IssueResponseTo update(@RequestBody IssueRequestTo request) {
        return issueService.update(request.id(), request);
    }

    /** Item-level update, kept as an alias of the collection-level operation. */
    @PutMapping("/{id}")
    public IssueResponseTo update(@PathVariable Long id, @RequestBody IssueRequestTo request) {
        return issueService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        issueService.delete(id);
    }

    /** Issues written by the given creator. */
    @GetMapping("/by-creator/{creatorId}")
    public List<IssueResponseTo> findByCreatorId(@PathVariable Long creatorId) {
        return issueService.findByCreatorId(creatorId);
    }

    /** Issues labelled with the given tag identifier. */
    @GetMapping("/by-tag/{tagId}")
    public List<IssueResponseTo> findByTagId(@PathVariable Long tagId) {
        return issueService.findByTagId(tagId);
    }

    /** Issues labelled with the given tag name. */
    @GetMapping("/by-tag-name/{tagName}")
    public List<IssueResponseTo> findByTagName(@PathVariable String tagName) {
        return issueService.findByTagName(tagName);
    }
}
