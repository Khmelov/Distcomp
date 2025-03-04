package com.bsuir.dc.controller;

import com.bsuir.dc.dto.request.TopicRequestTo;
import com.bsuir.dc.dto.response.TopicResponseTo;
import com.bsuir.dc.service.TopicService;
import com.bsuir.dc.util.validator.TopicValidator;
import com.bsuir.dc.util.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {
    private final TopicService topicService;
    private final TopicValidator topicValidator;

    @Autowired
    public TopicController(TopicService topicService, TopicValidator topicValidator) {
        this.topicService = topicService;
        this.topicValidator = topicValidator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponseTo createTopic(@RequestBody @Valid TopicRequestTo topicRequestTo, BindingResult bindingResult) {
        validate(topicRequestTo, bindingResult);
        return topicService.save(topicRequestTo);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TopicResponseTo> getAllTopics() { return topicService.findAll(); }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TopicResponseTo getTopicById(@PathVariable Long id) { return topicService.findById(id); }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public TopicResponseTo updateTopic(@RequestBody @Valid TopicRequestTo topicRequestTo, BindingResult bindingResult){
        validate(topicRequestTo, bindingResult);
        return topicService.update(topicRequestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTopic(@PathVariable long id){ topicService.deleteById(id); }

    private void validate(TopicRequestTo topicRequestTo, BindingResult bindingResult){
        topicValidator.validate(topicRequestTo, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
