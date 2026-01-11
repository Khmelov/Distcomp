package by.rest.publisher.controller;

import by.rest.publisher.client.CommentClient;
import by.rest.publisher.config.RedisCacheService;
import by.rest.publisher.dto.EditorResponseTo;
import by.rest.publisher.dto.TagResponseTo;
import by.rest.publisher.dto.StoryResponseTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.service.EditorService;
import by.rest.publisher.service.TagService;
import by.rest.publisher.service.StoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1.0/relations")
public class StoryRelationsController {
    
    private static final Logger log = LoggerFactory.getLogger(StoryRelationsController.class);
    
    private final StoryService storyService;
    private final EditorService editorService;
    private final TagService tagService;
    private final CommentClient commentClient;
    private final RedisCacheService redisCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public StoryRelationsController(StoryService storyService, 
                                   EditorService editorService,
                                   TagService tagService,
                                   CommentClient commentClient,
                                   RedisCacheService redisCacheService,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.storyService = storyService;
        this.editorService = editorService;
        this.tagService = tagService;
        this.commentClient = commentClient;
        this.redisCacheService = redisCacheService;
        this.redisTemplate = redisTemplate;
    }
    
    @GetMapping("/stories/{id}/editor")
    public EditorResponseTo getEditorByStory(@PathVariable Long id) {
        String cacheKey = "editor_by_story:" + id;
        
        Object cachedEditor = redisTemplate.opsForValue().get(cacheKey);
        if (cachedEditor != null) {
            log.info("Editor by story retrieved from cache: storyId={}", id);
            return (EditorResponseTo) cachedEditor;
        }
        
        StoryResponseTo story = storyService.getById(id);
        EditorResponseTo editor = editorService.getById(story.getEditorId());
        
        redisTemplate.opsForValue().set(cacheKey, editor, 6, TimeUnit.HOURS);
        
        return editor;
    }
    
    @GetMapping("/stories/{id}/comments")
    public List<CommentResponseTo> getCommentsByStory(@PathVariable Long id) {
        try {
            Object cachedComments = redisCacheService.getCommentsByStory(id);
            if (cachedComments != null) {
                log.info("Comments retrieved from Redis cache: storyId={}", id);
                return (List<CommentResponseTo>) cachedComments;
            }
            
            List<CommentResponseTo> comments = commentClient.getCommentsByStoryId(id);
            
            if (comments != null && !comments.isEmpty()) {
                redisCacheService.cacheCommentsByStory(id, comments);
                log.info("Comments cached in Redis: storyId={}, count={}", id, comments.size());
            }
            
            return comments;
        } catch (Exception e) {
            throw new ApiException(500, "50001", 
                "Failed to get comments from discussion module: " + e.getMessage());
        }
    }
    
    @GetMapping("/stories/{id}/tags")
    public List<TagResponseTo> getTagsByStory(@PathVariable Long id) {
        String cacheKey = "tags_by_story:" + id;
        
        Object cachedTags = redisTemplate.opsForValue().get(cacheKey);
        if (cachedTags != null) {
            log.info("Tags by story retrieved from cache: storyId={}", id);
            return (List<TagResponseTo>) cachedTags;
        }
        
        StoryResponseTo story = storyService.getById(id);
        if (story.getTagIds() == null || story.getTagIds().isEmpty()) {
            return List.of();
        }
        
        List<TagResponseTo> tags = story.getTagIds().stream()
                .map(tagService::getById)
                .toList();
        
        redisTemplate.opsForValue().set(cacheKey, tags, 12, TimeUnit.HOURS);
        
        return tags;
    }
    
    @GetMapping("/stories/{id}/full")
    public Map<String, Object> getFullStory(@PathVariable Long id) {
        String cacheKey = "full_story:" + id;
        
        Object cachedFullStory = redisCacheService.getStoryWithRelations(id);
        if (cachedFullStory != null) {
            log.info("Full story retrieved from cache: storyId={}", id);
            return (Map<String, Object>) cachedFullStory;
        }
        
        Map<String, Object> fullStory = new HashMap<>();
        
        StoryResponseTo story = storyService.getById(id);
        fullStory.put("story", story);
        
        try {
            EditorResponseTo editor = editorService.getById(story.getEditorId());
            fullStory.put("editor", editor);
        } catch (Exception e) {
            log.warn("Failed to get editor for story {}: {}", id, e.getMessage());
        }
        
        try {
            if (story.getTagIds() != null && !story.getTagIds().isEmpty()) {
                List<TagResponseTo> tags = story.getTagIds().stream()
                        .map(tagService::getById)
                        .toList();
                fullStory.put("tags", tags);
            }
        } catch (Exception e) {
            log.warn("Failed to get tags for story {}: {}", id, e.getMessage());
        }
        
        try {
            List<CommentResponseTo> comments = getCommentsByStory(id);
            fullStory.put("comments", comments);
        } catch (Exception e) {
            log.warn("Failed to get comments for story {}: {}", id, e.getMessage());
        }
        
        redisCacheService.cacheStoryWithRelations(id, fullStory);
        log.info("Full story cached: storyId={}", id);
        
        return fullStory;
    }
    
    @PostMapping("/stories/{id}/cache/clear")
    public ResponseEntity<Map<String, String>> clearStoryCache(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        
        try {
            redisTemplate.delete("editor_by_story:" + id);
            redisTemplate.delete("tags_by_story:" + id);
            redisCacheService.deleteStoryWithRelations(id);
            redisCacheService.deleteCommentsByStory(id);
            
            response.put("status", "SUCCESS");
            response.put("message", "Cache cleared for story: " + id);
            response.put("storyId", id.toString());
            
            log.info("Cache cleared for story: {}", id);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to clear cache: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public String health() {
        return "Story Relations Controller is working with Redis cache!";
    }
}