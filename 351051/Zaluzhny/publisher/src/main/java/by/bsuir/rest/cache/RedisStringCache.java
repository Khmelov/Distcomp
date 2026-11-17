package by.bsuir.rest.cache;

import org.springframework.data.redis.core.StringRedisTemplate;

final class RedisStringCache implements StringCache {

    private final StringRedisTemplate template;

    RedisStringCache(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public String get(String key) {
        return template.opsForValue().get(key);
    }

    @Override
    public void put(String key, String value) {
        template.opsForValue().set(key, value);
    }

    @Override
    public void delete(String key) {
        template.delete(key);
    }
}
