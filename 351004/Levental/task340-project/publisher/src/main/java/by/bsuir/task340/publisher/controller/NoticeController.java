package by.bsuir.task340.publisher.controller;

import by.bsuir.task340.publisher.dto.NoticeKafkaMessage;
import by.bsuir.task340.publisher.service.NoticeKafkaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {

    private final NoticeKafkaService noticeKafkaService;

    public NoticeController(NoticeKafkaService noticeKafkaService) {
        this.noticeKafkaService = noticeKafkaService;
    }

    @GetMapping
    public List<NoticeKafkaMessage> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long articleId
    ) {
        return noticeKafkaService.findAll(page, size, sort, filter, articleId);
    }

    @GetMapping("/{id}")
    public NoticeKafkaMessage findById(@PathVariable Long id) {
        return noticeKafkaService.findById(id);
    }

    @PostMapping
    public NoticeKafkaMessage create(@RequestBody NoticeKafkaMessage request) {
        return noticeKafkaService.create(request);
    }

    @PutMapping("/{id}")
    public NoticeKafkaMessage update(@PathVariable Long id,
                                     @RequestBody NoticeKafkaMessage request) {
        request.setId(id);
        return noticeKafkaService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        noticeKafkaService.delete(id);
    }
}