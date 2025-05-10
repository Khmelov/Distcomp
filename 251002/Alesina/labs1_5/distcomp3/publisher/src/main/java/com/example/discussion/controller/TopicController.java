package com.example.discussion.controller;

import com.example.discussion.dto.TopicRequestTo;
import com.example.discussion.dto.TopicResponseTo;
import com.example.discussion.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    private final TopicService topicService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TopicController(TopicService topicService, RedisTemplate<String, Object> redisTemplate) {
        this.topicService = topicService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAllTopics() {
        try {
            String cacheKey = "allTopics";
            List<TopicResponseTo> topicList = (List<TopicResponseTo>) redisTemplate.opsForValue().get(cacheKey);

            if (topicList == null) {
                topicList = topicService.findAll();
                redisTemplate.opsForValue().set(cacheKey, topicList, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(topicList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving topics list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicsById(@PathVariable Long id) {
        try {
            String cacheKey = "topics:" + id;
            TopicResponseTo topic = (TopicResponseTo) redisTemplate.opsForValue().get(cacheKey);


            if (topic == null) {
                topic = topicService.findById(id);
                if (topic != null) {
                    redisTemplate.opsForValue().set(cacheKey, topic, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("Topic not found", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(topic, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving topic item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody @Valid TopicRequestTo topicRequestTo) {
        try {
            TopicResponseTo createdTopic = topicService.save(topicRequestTo);
            redisTemplate.delete("allTopics");
            return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating topics", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTopic(@RequestBody @Valid TopicRequestTo topicRequestTo) {
        try {
            TopicResponseTo updatedTopic = topicService.update(topicRequestTo);
            if (updatedTopic != null) {
                redisTemplate.delete("allTopics");
                redisTemplate.delete("topics:" + updatedTopic.getId());
                return new ResponseEntity<>(updatedTopic, HttpStatus.OK);
            }
            return new ResponseEntity<>("Topic not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating topic", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        try {
            topicService.deleteById(id);
            redisTemplate.delete("allTopics");
            redisTemplate.delete("topics:" + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting topic", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}