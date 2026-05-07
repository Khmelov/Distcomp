package com.lizaveta.notebook.controller;

import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(final NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoticeResponseTo create(@Valid @RequestBody final NoticeRequestTo request) {
        return noticeService.create(request);
    }

    @GetMapping
    public Object findAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String sortBy,
            @RequestParam(defaultValue = "asc") final String sortOrder,
            @RequestParam(value = "format", required = false) final String format) {
        boolean wantList = "list".equalsIgnoreCase(format)
                || (format == null && page == 0 && size == 20 && (sortBy == null || sortBy.isBlank()));
        if (wantList) {
            return noticeService.findAll();
        }
        return noticeService.findAll(page, size, sortBy, sortOrder);
    }

    @GetMapping("/{id}")
    public NoticeResponseTo findById(@PathVariable final Long id) {
        return noticeService.findById(id);
    }

    @PutMapping("/{id}")
    public NoticeResponseTo update(
            @PathVariable final Long id,
            @Valid @RequestBody final NoticeRequestTo request) {
        return noticeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable final Long id) {
        noticeService.deleteById(id);
    }
}
