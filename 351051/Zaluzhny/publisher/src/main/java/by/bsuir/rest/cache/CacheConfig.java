package by.bsuir.rest.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "memory")
    StringCache memoryStringCache() {
        return new MemoryStringCache();
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "redis", matchIfMissing = true)
    StringCache redisStringCache(StringRedisTemplate template) {
        return new RedisStringCache(template);
    }

    @Bean
    ResponseCache responseCache(StringCache store, ObjectMapper objectMapper) {
        return new ResponseCache(store, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "redis", matchIfMissing = true)
    ApplicationRunner redisAvailabilityCheck(StringRedisTemplate template) {
        return new RedisAvailabilityCheck(template);
    }

    private static final class RedisAvailabilityCheck implements ApplicationRunner {

        private final StringRedisTemplate template;

        private RedisAvailabilityCheck(StringRedisTemplate template) {
            this.template = template;
        }

        @Override
        public void run(ApplicationArguments args) {
            template.opsForValue().get("distcomp:ping");
        }
    }
}
