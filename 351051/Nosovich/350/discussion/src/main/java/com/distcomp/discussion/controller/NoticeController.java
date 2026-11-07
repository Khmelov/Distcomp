package com.distcomp.discussion.controller;

import com.distcomp.common.notice.NoticeDto;
import com.distcomp.discussion.dto.NoticePage;
import com.distcomp.discussion.dto.NoticeRequestTo;
import com.distcomp.discussion.service.NoticeService;
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

/**
 * REST resource of the discussion module.
 *
 * <p>The same contract is exposed by the publisher on port 24110. Keeping the
 * controller here allows the discussion module to be tested and used directly on
 * port 24130 as required by the assignment.</p>
 */
@RestController
@RequestMapping(ApiPaths.V1 + "/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<NoticeDto> create(@RequestBody NoticeRequestTo request) {
        NoticeDto created = noticeService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<NoticeDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Long issueId) {
        NoticePage result = noticeService.findAll(issueId, page, size, sort);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.total()))
                .body(result.content());
    }

    @GetMapping("/{id}")
    public NoticeDto findById(@PathVariable Long id) {
        return noticeService.findById(id);
    }

    @PutMapping
    public NoticeDto update(@RequestBody NoticeRequestTo request) {
        return noticeService.update(request.id(), request);
    }

    @PutMapping("/{id}")
    public NoticeDto update(@PathVariable Long id, @RequestBody NoticeRequestTo request) {
        return noticeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noticeService.delete(id);
    }

    @GetMapping("/by-issue/{issueId}")
    public List<NoticeDto> findByIssueId(@PathVariable Long issueId) {
        return noticeService.findAll(issueId, null, null, "id,asc").content();
    }

    @DeleteMapping("/by-issue/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByIssueId(@PathVariable Long issueId) {
        noticeService.deleteByIssueId(issueId);
    }
}
