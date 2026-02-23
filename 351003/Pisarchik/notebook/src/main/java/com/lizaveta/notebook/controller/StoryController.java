package com.lizaveta.notebook.controller;

import com.lizaveta.notebook.model.dto.request.StoryRequestTo;
import com.lizaveta.notebook.model.dto.response.MarkerResponseTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.StoryResponseTo;
import com.lizaveta.notebook.model.dto.response.WriterResponseTo;
import com.lizaveta.notebook.service.MarkerService;
import com.lizaveta.notebook.service.NoticeService;
import com.lizaveta.notebook.service.StoryService;
import com.lizaveta.notebook.service.WriterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

import java.util.List;
import java.util.Set;

/**
 * REST controller for Story CRUD operations.
 */
@RestController
@RequestMapping("/api/v1.0/stories")
public class StoryController {

    private final StoryService storyService;
    private final WriterService writerService;
    private final MarkerService markerService;
    private final NoticeService noticeService;

    public StoryController(
            final StoryService storyService,
            final WriterService writerService,
            final MarkerService markerService,
            final NoticeService noticeService) {
        this.storyService = storyService;
        this.writerService = writerService;
        this.markerService = markerService;
        this.noticeService = noticeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo create(@Valid @RequestBody final StoryRequestTo request) {
        return storyService.create(request);
    }

    @GetMapping
    public List<StoryResponseTo> findAll(
            @RequestParam(required = false) final Set<Long> markerIds,
            @RequestParam(required = false) final String writerLogin,
            @RequestParam(required = false) final String title,
            @RequestParam(required = false) final String content) {
        if (markerIds != null || writerLogin != null || title != null || content != null) {
            return storyService.findByMarkerIdsAndWriterLoginAndTitleAndContent(
                    markerIds, writerLogin, title, content);
        }
        return storyService.findAll();
    }

    @GetMapping("/{id}")
    public StoryResponseTo findById(@PathVariable final Long id) {
        return storyService.findById(id);
    }

    @GetMapping("/{id}/writer")
    public WriterResponseTo findWriterByStoryId(@PathVariable final Long id) {
        return writerService.findByStoryId(id);
    }

    @GetMapping("/{id}/markers")
    public List<MarkerResponseTo> findMarkersByStoryId(@PathVariable final Long id) {
        return markerService.findByStoryId(id);
    }

    @GetMapping("/{id}/notices")
    public List<NoticeResponseTo> findNoticesByStoryId(@PathVariable final Long id) {
        return noticeService.findByStoryId(id);
    }

    @PutMapping("/{id}")
    public StoryResponseTo update(
            @PathVariable final Long id,
            @Valid @RequestBody final StoryRequestTo request) {
        return storyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable final Long id) {
        storyService.deleteById(id);
    }
}
