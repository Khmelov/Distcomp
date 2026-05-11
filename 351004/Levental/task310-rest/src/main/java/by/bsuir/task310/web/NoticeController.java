package by.bsuir.task310.web;

import by.bsuir.task310.dto.request.NoticeRequestTo;
import by.bsuir.task310.dto.response.NoticeResponseTo;
import by.bsuir.task310.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {

    private final NoticeService service;

    public NoticeController(NoticeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping
    public ResponseEntity<NoticeResponseTo> update(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}