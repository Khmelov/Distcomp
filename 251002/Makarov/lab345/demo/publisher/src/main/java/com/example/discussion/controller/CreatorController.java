package com.example.discussion.controller;

import com.example.discussion.dto.CreatorRequestTo;
import com.example.discussion.dto.CreatorResponseTo;
import com.example.discussion.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    private final CreatorService creatorService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CreatorController(CreatorService creatorService, RedisTemplate<String, Object> redisTemplate) {
        this.creatorService = creatorService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAllCreators() {
        try {
            String cacheKey = "allCreators";
            List<CreatorResponseTo> creators = (List<CreatorResponseTo>) redisTemplate.opsForValue().get(cacheKey);

            if (creators == null) {
                creators = creatorService.findAll();
                redisTemplate.opsForValue().set(cacheKey, creators, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(creators, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching creators", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCreatorById(@PathVariable Long id) {
        try {
            String cacheKey = "creator:" + id;
            CreatorResponseTo creator = (CreatorResponseTo) redisTemplate.opsForValue().get(cacheKey);

            if (creator == null) {
                creator = creatorService.findById(id);
                if (creator != null) {
                    redisTemplate.opsForValue().set(cacheKey, creator, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("creator not found", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(creator, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createCreator(@Valid @RequestBody CreatorRequestTo creatorRequestTo) {
        CreatorResponseTo savedCreator = creatorService.save(creatorRequestTo);
        redisTemplate.delete("allCreators"); // Инвалидация кэша
        return new ResponseEntity<>(savedCreator, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateCreator(@Valid @RequestBody CreatorRequestTo creatorRequestTo) {
        try {
            CreatorResponseTo updatedCreator = creatorService.update(creatorRequestTo);
            if (updatedCreator != null) {
                redisTemplate.delete("allCreators");
                redisTemplate.delete("creator:" + updatedCreator.getId());
                return new ResponseEntity<>(updatedCreator, HttpStatus.OK);
            }
            return new ResponseEntity<>("creator not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating creator", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCreator(@PathVariable Long id) {
        try {
            creatorService.deleteById(id);
            redisTemplate.delete("allCreators");
            redisTemplate.delete("creator:" + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting creator", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}