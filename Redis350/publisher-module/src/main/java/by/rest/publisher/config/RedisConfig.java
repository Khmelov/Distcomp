package by.rest.publisher.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(redisProperties.getTtl().getHours()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()
                .prefixCacheNameWith("publisher:");

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("editors", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getEditors())))
                .withCacheConfiguration("stories", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getStories())))
                .withCacheConfiguration("tags", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getTags())))
                .withCacheConfiguration("comments", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getComments())))
                .withCacheConfiguration("allEditors", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getEditors())))
                .withCacheConfiguration("allStories", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getStories())))
                .withCacheConfiguration("allTags", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getTags())))
                .withCacheConfiguration("allComments", 
                    defaultConfig.entryTtl(Duration.ofHours(redisProperties.getTtl().getComments())))
                .transactionAware()
                .build();
    }

    @Bean
    public RedisCacheService redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheService(redisTemplate, redisProperties);
    }
}