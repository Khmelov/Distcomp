package com.distcomp.controller;

import com.distcomp.dto.filter.NoticeFilter;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.pagination.PageRequests;
import com.distcomp.pagination.PagedResult;
import com.distcomp.service.NoticeService;
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
 * REST resource of the notices.
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/notices")
public class NoticeController {

    private static final Set<String> SORTABLE = Set.of("id", "content");

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@RequestBody NoticeRequestTo request) {
        NoticeResponseTo created = noticeService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * @param page    zero-based page number, optional
     * @param size    page size, optional
     * @param sort    sort expression, defaults to {@code id,asc}
     * @param issueId optional related issue identifier
     */
    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Long issueId) {

        PagedResult<NoticeResponseTo> result = noticeService.findAll(
                new NoticeFilter(issueId), PageRequests.of(page, size, sort, "id,asc", SORTABLE));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.total()))
                .body(result.content());
    }

    @GetMapping("/{id}")
    public NoticeResponseTo findById(@PathVariable Long id) {
        return noticeService.findById(id);
    }

    /** Collection-level update: the identifier travels inside the payload. */
    @PutMapping
    public NoticeResponseTo update(@RequestBody NoticeRequestTo request) {
        return noticeService.update(request.id(), request);
    }

    /** Item-level update, kept as an alias of the collection-level operation. */
    @PutMapping("/{id}")
    public NoticeResponseTo update(@PathVariable Long id, @RequestBody NoticeRequestTo request) {
        return noticeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noticeService.delete(id);
    }

    /** Notices attached to the given issue. */
    @GetMapping("/by-issue/{issueId}")
    public List<NoticeResponseTo> findByIssueId(@PathVariable Long issueId) {
        return noticeService.findByIssueId(issueId);
    }
}
