package by.liza.app.controller;

import by.liza.app.dto.request.WriterRequestTo;
import by.liza.app.dto.response.WriterResponseTo;
import by.liza.app.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/writers")
@RequiredArgsConstructor
public class WriterV2Controller {

    private final WriterService writerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterResponseTo create(@Valid @RequestBody WriterRequestTo req) {
        return writerService.create(req);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WriterResponseTo> getAll() {
        return writerService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo getById(@PathVariable Long id) {
        return writerService.getById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @writerSecurity.isOwner(authentication, #req.getId())")
    public WriterResponseTo update(@Valid @RequestBody WriterRequestTo req) {
        return writerService.update(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        writerService.delete(id);
    }
}
