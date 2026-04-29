package by.distcomp.app.config;

import by.distcomp.app.dto.NoteResponseTo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, NoteResponseTo> noteRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, NoteResponseTo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<NoteResponseTo> serializer = new Jackson2JsonRedisSerializer<>(NoteResponseTo.class);
        template.setValueSerializer(serializer);

        return template;
    }
}
