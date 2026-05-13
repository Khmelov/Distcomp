package com.bsuir.distcomp.controller.v1;

import com.bsuir.distcomp.dto.TopicRequestTo;
import com.bsuir.distcomp.dto.TopicResponseTo;
import com.bsuir.distcomp.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponseTo create(@RequestBody @Valid TopicRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<TopicResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TopicResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public TopicResponseTo update(
            @PathVariable Long id,
            @RequestBody @Valid TopicRequestTo dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}