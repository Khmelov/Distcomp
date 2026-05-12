package com.bsuir.distcomp.controller.v1;

import com.bsuir.distcomp.dto.WriterRequestTo;
import com.bsuir.distcomp.dto.WriterResponseTo;
import com.bsuir.distcomp.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {

    private final WriterService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterResponseTo create(@RequestBody @Valid WriterRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<WriterResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public WriterResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public WriterResponseTo update(
            @PathVariable Long id,
            @RequestBody @Valid WriterRequestTo dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

