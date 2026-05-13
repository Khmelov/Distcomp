package by.bsuir.task330.discussion.controller;

import by.bsuir.task330.discussion.dto.NoticeRequestTo;
import by.bsuir.task330.discussion.dto.NoticeResponseTo;
import by.bsuir.task330.discussion.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public NoticeResponseTo create(@Valid @RequestBody NoticeRequestTo request) {
        return service.create(request);
    }

    @PutMapping
    public NoticeResponseTo update(@Valid @RequestBody NoticeRequestTo request) {
        return service.update(request);
    }

    @GetMapping("/{id}")
    public NoticeResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<NoticeResponseTo> findAll(@RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false) String filter,
                                          @RequestParam(required = false) Long articleId) {
        return service.findAll(page, size, sort, filter, articleId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
