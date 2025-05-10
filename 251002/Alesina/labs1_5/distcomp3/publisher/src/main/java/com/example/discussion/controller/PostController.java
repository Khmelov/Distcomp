package com.example.discussion.controller;

import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import com.example.discussion.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {

    private final PostService postService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public PostController(PostService postService, RedisTemplate<String, Object> redisTemplate) {
        this.postService = postService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            String cacheKey = "allPosts";
            List<PostResponseTo> posts = (List<PostResponseTo>) redisTemplate.opsForValue().get(cacheKey);
            if (posts == null) {
                posts = postService.findAll();
                redisTemplate.opsForValue().set(cacheKey, posts, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            String cacheKey = "post:" + id;
            PostResponseTo post = (PostResponseTo) redisTemplate.opsForValue().get(cacheKey);
            if (post == null) {
                post = postService.findById(id);
                if (post != null) {
                    redisTemplate.opsForValue().set(cacheKey, post, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("Post does not exist", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>("Post does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody @Valid PostRequestTo postRequestTo) {
        try {
            PostResponseTo createdPost = postService.save(postRequestTo);
            redisTemplate.delete("allPosts");
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> updatePost(@RequestBody @Valid PostRequestTo postRequestTo) {
        try {
            PostResponseTo updatedPost = postService.update(postRequestTo);
            if (updatedPost != null) {
                redisTemplate.delete("allPosts");
                redisTemplate.delete("post:" + updatedPost.getId());
                return new ResponseEntity<>(updatedPost, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deleteById(id);
            redisTemplate.delete("allPosts");
            redisTemplate.delete("post:" + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}