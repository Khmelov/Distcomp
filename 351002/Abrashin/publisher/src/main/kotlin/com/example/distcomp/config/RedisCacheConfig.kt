package com.example.distcomp.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisCacheConfig {
    @Bean
    fun redisValueSerializer(objectMapper: ObjectMapper): GenericJackson2JsonRedisSerializer {
        val redisObjectMapper = objectMapper.copy().apply {
            activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
            )
        }
        return GenericJackson2JsonRedisSerializer(redisObjectMapper)
    }

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        redisValueSerializer: GenericJackson2JsonRedisSerializer
    ): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            valueSerializer = redisValueSerializer
            hashValueSerializer = redisValueSerializer
            afterPropertiesSet()
        }
}
