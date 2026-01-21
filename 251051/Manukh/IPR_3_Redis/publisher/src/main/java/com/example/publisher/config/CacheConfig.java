package com.example.publisher.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfig {

    public static class CacheNames {
        public static final String EDITORS = "editors";
        public static final String EDITOR_BY_ID = "editorById";
        public static final String STORIES = "stories";
        public static final String STORY_BY_ID = "storyById";
        public static final String MARKS = "marks";
        public static final String MARK_BY_ID = "markById";
        public static final String STORIES_BY_EDITOR = "storiesByEditor";
        public static final String MARKS_BY_STORY = "marksByStory";
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                CacheNames.EDITORS,
                CacheNames.EDITOR_BY_ID,
                CacheNames.STORIES,
                CacheNames.STORY_BY_ID,
                CacheNames.MARKS,
                CacheNames.MARK_BY_ID,
                CacheNames.STORIES_BY_EDITOR,
                CacheNames.MARKS_BY_STORY
        );
    }
}