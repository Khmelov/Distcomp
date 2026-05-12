package com.example.task310.controller;

import com.example.task310.dto.NoticeRequestTo;
import com.example.task310.dto.NoticeResponseTo;
import com.example.task310.dto.kafka.NoticeMessage;
import com.example.task310.model.NoticeState;
import com.example.task310.service.NoticeKafkaService;
import com.example.task310.service.NoticeRestClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeKafkaService noticeKafkaService;
    private final NoticeRestClientService noticeRestClientService;

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo request) {
        NoticeMessage response = noticeKafkaService.sendForModeration(request.getNewsId(), request.getContent());
        NoticeResponseTo dto = new NoticeResponseTo();
        dto.setId(response.getId());
        dto.setNewsId(response.getNewsId());
        dto.setContent(response.getContent());
        // Преобразуем String в NoticeState
        dto.setState(NoticeState.valueOf(response.getState()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> findAll() {
        return ResponseEntity.ok(noticeRestClientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeRestClientService.findById(id));
    }

    @PutMapping
    public ResponseEntity<NoticeResponseTo> update(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.ok(noticeRestClientService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeRestClientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}