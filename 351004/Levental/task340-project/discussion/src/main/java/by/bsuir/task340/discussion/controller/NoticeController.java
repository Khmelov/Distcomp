package by.bsuir.task340.discussion.controller;

import by.bsuir.task340.discussion.dto.response.NoticeResponseTo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {

    @GetMapping
    public List<NoticeResponseTo> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long articleId
    ) {
        return new ArrayList<>();
    }

    @GetMapping("/{id}")
    public NoticeResponseTo findById(@PathVariable Long id) {
        return new NoticeResponseTo(id, 0L, "", "PENDING");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    }
}