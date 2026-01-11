package by.rest.publisher.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    
    // Префиксы для ключей
    private static final String EDITOR_PREFIX = "editor:";
    private static final String STORY_PREFIX = "story:";
    private static final String TAG_PREFIX = "tag:";
    private static final String COMMENT_PREFIX = "comment:";
    private static final String ALL_EDITORS_KEY = "all_editors";
    private static final String ALL_STORIES_KEY = "all_stories";
    private static final String ALL_TAGS_KEY = "all_tags";
    private static final String ALL_COMMENTS_KEY = "all_comments";
    private static final String STORY_WITH_RELATIONS_PREFIX = "story_relations:";
    private static final String COMMENTS_BY_STORY_PREFIX = "comments_by_story:";
    
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, 
                            RedisProperties redisProperties) {
        this.redisTemplate = redisTemplate;
        this.redisProperties = redisProperties;
    }
    
    // ========== Editor методы ==========
    public void cacheEditor(Long id, Object editor) {
        String key = EDITOR_PREFIX + id;
        redisTemplate.opsForValue().set(key, editor, 
            redisProperties.getTtl().getEditors(), TimeUnit.HOURS);
        log.debug("Editor cached: id={}, ttl={} hours", id, redisProperties.getTtl().getEditors());
    }
    
    public Object getEditor(Long id) {
        String key = EDITOR_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Editor retrieved from cache: id={}", id);
        }
        return cached;
    }
    
    public void deleteEditor(Long id) {
        String key = EDITOR_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.delete(ALL_EDITORS_KEY);
        log.debug("Editor cache deleted: id={}", id);
    }
    
    public void cacheAllEditors(Object editors) {
        redisTemplate.opsForValue().set(ALL_EDITORS_KEY, editors, 
            redisProperties.getTtl().getEditors(), TimeUnit.HOURS);
        log.debug("All editors cached");
    }
    
    public Object getAllEditors() {
        Object cached = redisTemplate.opsForValue().get(ALL_EDITORS_KEY);
        if (cached != null) {
            log.debug("All editors retrieved from cache");
        }
        return cached;
    }
    
    // ========== Story методы ==========
    public void cacheStory(Long id, Object story) {
        String key = STORY_PREFIX + id;
        redisTemplate.opsForValue().set(key, story, 
            redisProperties.getTtl().getStories(), TimeUnit.HOURS);
        log.debug("Story cached: id={}, ttl={} hours", id, redisProperties.getTtl().getStories());
    }
    
    public Object getStory(Long id) {
        String key = STORY_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Story retrieved from cache: id={}", id);
        }
        return cached;
    }
    
    public void deleteStory(Long id) {
        String key = STORY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.delete(ALL_STORIES_KEY);
        log.debug("Story cache deleted: id={}", id);
    }
    
    public void cacheAllStories(Object stories) {
        redisTemplate.opsForValue().set(ALL_STORIES_KEY, stories, 
            redisProperties.getTtl().getStories(), TimeUnit.HOURS);
        log.debug("All stories cached");
    }
    
    public Object getAllStories() {
        Object cached = redisTemplate.opsForValue().get(ALL_STORIES_KEY);
        if (cached != null) {
            log.debug("All stories retrieved from cache");
        }
        return cached;
    }
    
    // ========== Tag методы ==========
    public void cacheTag(Long id, Object tag) {
        String key = TAG_PREFIX + id;
        redisTemplate.opsForValue().set(key, tag, 
            redisProperties.getTtl().getTags(), TimeUnit.HOURS);
        log.debug("Tag cached: id={}, ttl={} hours", id, redisProperties.getTtl().getTags());
    }
    
    public Object getTag(Long id) {
        String key = TAG_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Tag retrieved from cache: id={}", id);
        }
        return cached;
    }
    
    public void deleteTag(Long id) {
        String key = TAG_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.delete(ALL_TAGS_KEY);
        log.debug("Tag cache deleted: id={}", id);
    }
    
    public void cacheAllTags(Object tags) {
        redisTemplate.opsForValue().set(ALL_TAGS_KEY, tags, 
            redisProperties.getTtl().getTags(), TimeUnit.HOURS);
        log.debug("All tags cached");
    }
    
    public Object getAllTags() {
        Object cached = redisTemplate.opsForValue().get(ALL_TAGS_KEY);
        if (cached != null) {
            log.debug("All tags retrieved from cache");
        }
        return cached;
    }
    
    // ========== Comment методы ==========
    public void cacheComment(String id, Object comment) {
        String key = COMMENT_PREFIX + id;
        redisTemplate.opsForValue().set(key, comment, 
            redisProperties.getTtl().getComments(), TimeUnit.HOURS);
        log.debug("Comment cached: id={}, ttl={} hours", id, redisProperties.getTtl().getComments());
    }
    
    public Object getComment(String id) {
        String key = COMMENT_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Comment retrieved from cache: id={}", id);
        }
        return cached;
    }
    
    public void deleteComment(String id) {
        String key = COMMENT_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.delete(ALL_COMMENTS_KEY);
        log.debug("Comment cache deleted: id={}", id);
    }
    
    public void cacheAllComments(Object comments) {
        redisTemplate.opsForValue().set(ALL_COMMENTS_KEY, comments, 
            redisProperties.getTtl().getComments(), TimeUnit.HOURS);
        log.debug("All comments cached");
    }
    
    public Object getAllComments() {
        Object cached = redisTemplate.opsForValue().get(ALL_COMMENTS_KEY);
        if (cached != null) {
            log.debug("All comments retrieved from cache");
        }
        return cached;
    }
    
    // ========== Story with relations ==========
    public void cacheStoryWithRelations(Long storyId, Object storyWithRelations) {
        String key = STORY_WITH_RELATIONS_PREFIX + storyId;
        redisTemplate.opsForValue().set(key, storyWithRelations,
            redisProperties.getTtl().getStories(), TimeUnit.HOURS);
        log.debug("Story with relations cached: storyId={}", storyId);
    }
    
    public Object getStoryWithRelations(Long storyId) {
        String key = STORY_WITH_RELATIONS_PREFIX + storyId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Story with relations retrieved from cache: storyId={}", storyId);
        }
        return cached;
    }
    
    public void deleteStoryWithRelations(Long storyId) {
        String key = STORY_WITH_RELATIONS_PREFIX + storyId;
        redisTemplate.delete(key);
        log.debug("Story with relations cache deleted: storyId={}", storyId);
    }
    
    // ========== Comments by story ==========
    public void cacheCommentsByStory(Long storyId, Object comments) {
        String key = COMMENTS_BY_STORY_PREFIX + storyId;
        redisTemplate.opsForValue().set(key, comments,
            redisProperties.getTtl().getComments(), TimeUnit.HOURS);
        log.debug("Comments by story cached: storyId={}", storyId);
    }
    
    public Object getCommentsByStory(Long storyId) {
        String key = COMMENTS_BY_STORY_PREFIX + storyId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Comments by story retrieved from cache: storyId={}", storyId);
        }
        return cached;
    }
    
    public void deleteCommentsByStory(Long storyId) {
        String key = COMMENTS_BY_STORY_PREFIX + storyId;
        redisTemplate.delete(key);
        log.debug("Comments by story cache deleted: storyId={}", storyId);
    }
    
    // ========== Общие методы ==========
    public void clearAllCache() {
        redisTemplate.delete(redisTemplate.keys(EDITOR_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(STORY_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(TAG_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(COMMENT_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(STORY_WITH_RELATIONS_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(COMMENTS_BY_STORY_PREFIX + "*"));
        redisTemplate.delete(ALL_EDITORS_KEY);
        redisTemplate.delete(ALL_STORIES_KEY);
        redisTemplate.delete(ALL_TAGS_KEY);
        redisTemplate.delete(ALL_COMMENTS_KEY);
        log.info("All cache cleared");
    }
    
    public boolean isCacheAvailable() {
        try {
            String result = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
            return "PONG".equals(result);
        } catch (Exception e) {
            log.warn("Redis is not available: {}", e.getMessage());
            return false;
        }
    }
    
    // ========== Cache Stats ==========
    public CacheStats getCacheStats() {
        CacheStats stats = new CacheStats();
        
        // Подсчитываем количество ключей по каждому типу
        stats.editorsCount = redisTemplate.keys(EDITOR_PREFIX + "*").size();
        stats.storiesCount = redisTemplate.keys(STORY_PREFIX + "*").size();
        stats.tagsCount = redisTemplate.keys(TAG_PREFIX + "*").size();
        stats.commentsCount = redisTemplate.keys(COMMENT_PREFIX + "*").size();
        stats.available = isCacheAvailable();
        stats.timestamp = Instant.now();
        
        return stats;
    }
    
    // Простой внутренний класс для статистики
    public static class CacheStats {
        public long editorsCount;
        public long storiesCount;
        public long tagsCount;
        public long commentsCount;
        public boolean available;
        public Instant timestamp;
        
        // Геттеры
        public long getEditorsCount() { return editorsCount; }
        public long getStoriesCount() { return storiesCount; }
        public long getTagsCount() { return tagsCount; }
        public long getCommentsCount() { return commentsCount; }
        public boolean isAvailable() { return available; }
        public Instant getTimestamp() { return timestamp; }
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
}