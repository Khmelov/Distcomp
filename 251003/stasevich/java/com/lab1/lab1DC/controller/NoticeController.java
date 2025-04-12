package com.lab1.lab1DC.controller;

import com.lab1.lab1DC.dto.NoticeRequestTo;
import com.lab1.lab1DC.dto.NoticeResponseTo;
import com.lab1.lab1DC.dto.UserRequestTo;
import com.lab1.lab1DC.dto.UserResponseTo;
import com.lab1.lab1DC.entity.Notice;
import com.lab1.lab1DC.service.NoticeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> createNotice(@RequestBody NoticeRequestTo noticeRequestTo) {
        try {
            if(!validateNotice(noticeRequestTo)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NoticeResponseTo());
            }

            Long id = System.currentTimeMillis();
            Notice notice = new Notice(id, noticeRequestTo.getContent(), noticeRequestTo.getStoryId());
            NoticeResponseTo response = noticeService.createNotice(notice);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new NoticeResponseTo());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new NoticeResponseTo());
        }
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAllNotices() {
        try {
            List<NoticeResponseTo> notices = noticeService.getAllNotices();
            return ResponseEntity.ok(notices);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getNoticeById(@PathVariable Long id) {
        try {
            NoticeResponseTo notice = noticeService.getNoticeById(id);
            return ResponseEntity.ok(notice);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<NoticeResponseTo> updateNotice(@RequestBody NoticeRequestTo noticeRequestTo) {
        if (noticeRequestTo.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            NoticeResponseTo updatedNotice = noticeService.updateNotice(noticeRequestTo.getId(), noticeRequestTo);
            return ResponseEntity.ok(updatedNotice);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception x) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    private boolean validateNotice(NoticeRequestTo noticeRequestTo) {

        if (noticeRequestTo.getContent().length() < 2 ||
                noticeRequestTo.getContent().length() > 2048) {
            return false;
        }

        return true;
    }
}
