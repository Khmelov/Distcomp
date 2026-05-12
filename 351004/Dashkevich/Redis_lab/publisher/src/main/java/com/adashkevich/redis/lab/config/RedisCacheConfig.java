package com.adashkevich.redis.lab.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String EDITORS = "editors";
    public static final String EDITOR = "editor";
    public static final String MARKERS = "markers";
    public static final String MARKER = "marker";
    public static final String NEWS = "news";
    public static final String NEWS_ITEM = "newsItem";
    public static final String MESSAGES = "messages";
    public static final String MESSAGE = "message";
    public static final String MESSAGES_BY_NEWS = "messagesByNews";
    public static final String NEWS_EDITOR = "newsEditor";
    public static final String NEWS_MARKERS = "newsMarkers";

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.adashkevich.kafka.lab")
                        .allowIfSubType("com.adashkevich.redis.lab")
                        .allowIfSubType("java.util")
                        .allowIfSubType("java.lang")
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        RedisCacheConfiguration base = redisCacheConfiguration();
        return builder -> builder
                .withCacheConfiguration(EDITORS, base.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration(EDITOR, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(MARKERS, base.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration(MARKER, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(NEWS, base.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration(NEWS_ITEM, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(MESSAGES, base.entryTtl(Duration.ofMinutes(2)))
                .withCacheConfiguration(MESSAGE, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(MESSAGES_BY_NEWS, base.entryTtl(Duration.ofMinutes(2)))
                .withCacheConfiguration(NEWS_EDITOR, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(NEWS_MARKERS, base.entryTtl(Duration.ofMinutes(10)));
    }
}
