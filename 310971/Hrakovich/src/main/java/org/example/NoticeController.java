package org.example;

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

    private final NoticeService noticeService;

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll() {
        return ResponseEntity.ok(noticeService.getAll());
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(
            @RequestBody @Valid NoticeRequestTo dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noticeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> update(
            @PathVariable Long id,
            @RequestBody @Valid NoticeRequestTo dto
    ) {
        return ResponseEntity.ok(noticeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}