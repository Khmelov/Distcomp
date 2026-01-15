package com.blog.controller;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
public class WriterTopicController {

    @Autowired
    private TopicService topicService;

    @PostMapping("/{writerId}/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponseTo createTopicForWriter(@PathVariable Long writerId,
                                                @Valid @RequestBody TopicRequestTo request) {
        // Устанавливаем writerId из пути
        request.setWriterId(writerId);
        return topicService.create(request);
    }

    @GetMapping("/{writerId}/topics")
    @ResponseStatus(HttpStatus.OK)
    public List<TopicResponseTo> getTopicsForWriter(@PathVariable Long writerId) {
        return topicService.getByEditorId(writerId);
    }
}