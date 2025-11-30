package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.NoticeControllerApi;
import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import com.rest.restapp.service.NoticeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NoticeController implements NoticeControllerApi {

    NoticeService noticeService;

    @PostMapping("/notices")
    public ResponseEntity<NoticeResponseToDto> createNotice(NoticeRequestToDto requestTo) {
        var response = noticeService.createNotice(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<NoticeResponseToDto> getNoticeById(Long id) {
        var response = noticeService.getNoticeById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<NoticeResponseToDto>> getAllNotices() {
        var responses = noticeService.getAllNotices();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<NoticeResponseToDto> updateNotice(Long id, NoticeRequestToDto requestTo) {
        var response = noticeService.updateNotice(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteNotice(Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<NoticeResponseToDto>> getNoticesByIssueId(Long issueId) {
        var responses = noticeService.getNoticesByIssueId(issueId);
        return ResponseEntity.ok(responses);
    }
}