package com.homel.user_stories.controller;

import com.homel.user_stories.dto.NoticeRequestTo;
import com.homel.user_stories.dto.NoticeResponseTo;
import com.homel.user_stories.dto.StoryRequestTo;
import com.homel.user_stories.dto.StoryResponseTo;
import com.homel.user_stories.service.NoticeService;
import com.homel.user_stories.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoticeResponseTo createNotice(@Valid @RequestBody NoticeRequestTo noticeRequest) {
        return noticeService.createNotice(noticeRequest);
    }

    @GetMapping("/{id}")
    public NoticeResponseTo getNotice(@PathVariable Long id) {
        return noticeService.getNotice(id);
    }

    @GetMapping
    public List<NoticeResponseTo> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
    }

    @PutMapping
    public NoticeResponseTo updateNotice(@Valid @RequestBody NoticeRequestTo noticeRequest) {
        return noticeService.updateNotice(noticeRequest);
    }
}
