package by.liza.app.controller;

import by.liza.app.dto.request.WriterRequestTo;
import by.liza.app.dto.response.WriterResponseTo;
import by.liza.app.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {

    private final WriterService writerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterResponseTo create(@Valid @RequestBody WriterRequestTo requestTo) {
        return writerService.create(requestTo);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo getById(@PathVariable Long id) {
        return writerService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WriterResponseTo> getAll() {
        return writerService.getAll();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo update(@Valid @RequestBody WriterRequestTo requestTo) {
        return writerService.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        writerService.delete(id);
    }
}