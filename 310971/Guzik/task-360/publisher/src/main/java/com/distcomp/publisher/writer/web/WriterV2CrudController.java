package com.distcomp.publisher.writer.web;

import com.distcomp.publisher.writer.dto.WriterRequest;
import com.distcomp.publisher.writer.dto.WriterResponse;
import com.distcomp.publisher.writer.service.WriterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0/writers")
public class WriterV2CrudController {

    private final WriterService service;

    public WriterV2CrudController(WriterService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponse> get(@PathVariable long id) {
        return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<WriterResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PreAuthorize("@access.canWriteWriter(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<WriterResponse> update(@PathVariable long id, @Valid @RequestBody WriterRequest request) {
        return service.update(id, request).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@access.canWriteWriter(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
