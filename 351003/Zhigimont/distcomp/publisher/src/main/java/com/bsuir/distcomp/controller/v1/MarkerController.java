package com.bsuir.distcomp.controller.v1;

import com.bsuir.distcomp.dto.MarkerRequestTo;
import com.bsuir.distcomp.dto.MarkerResponseTo;
import com.bsuir.distcomp.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@RequestBody @Valid MarkerRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<MarkerResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(
            @PathVariable Long id,
            @RequestBody @Valid MarkerRequestTo dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
