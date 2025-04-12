package org.ex.distributed_computing.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.NoticeRequestDTO;
import org.ex.distributed_computing.dto.response.NoticeResponseDTO;
import org.ex.distributed_computing.service.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @GetMapping
  public List<NoticeResponseDTO> getAllNotices() {
    return noticeService.getAllNotices();
  }

  @GetMapping("/{id}")
  public ResponseEntity<NoticeResponseDTO> getNoticeById(@PathVariable Long id) {
    return ResponseEntity.ok(noticeService.getNoticeById(id));
  }

  @PostMapping
  public ResponseEntity<NoticeResponseDTO> createNotice(@Valid @RequestBody NoticeRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.createNotice(requestDTO));
  }

  @PutMapping
  public ResponseEntity<NoticeResponseDTO> updateNotice(@Valid @RequestBody NoticeRequestDTO requestDTO) {
    return ResponseEntity.ok(noticeService.updateNotice(requestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
    noticeService.deleteNotice(id);
    return ResponseEntity.noContent().build();
  }
}

