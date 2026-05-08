package by.liza.app.controller;

import by.liza.app.dto.request.MarkRequestTo;
import by.liza.app.dto.response.MarkResponseTo;
import by.liza.app.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkResponseTo create(@Valid @RequestBody MarkRequestTo requestTo) {
        return markService.create(requestTo);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MarkResponseTo getById(@PathVariable Long id) {
        return markService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MarkResponseTo> getAll() {
        return markService.getAll();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MarkResponseTo update(@Valid @RequestBody MarkRequestTo requestTo) {
        return markService.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        markService.delete(id);
    }
}