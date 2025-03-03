package com.example.lab1.controller;

import com.example.lab1.dto.TopicRequestTo;
import com.example.lab1.dto.TopicResponseTo;
import com.example.lab1.dto.UserRequestTo;
import com.example.lab1.dto.UserResponseTo;
import com.example.lab1.service.TopicService;
import com.example.lab1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<TopicResponseTo> getTopics() {
        return topicService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public TopicResponseTo getTopic(@PathVariable long id) {
        return topicService.getById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TopicResponseTo createTopic(@Valid @RequestBody TopicRequestTo topicRequestTo) {
        return topicService.create(topicRequestTo);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public TopicResponseTo updateTopic(@Valid @RequestBody TopicRequestTo topicRequestTo) {
        return topicService.update(topicRequestTo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable Long id) {
        topicService.deleteById(id);
    }
}
