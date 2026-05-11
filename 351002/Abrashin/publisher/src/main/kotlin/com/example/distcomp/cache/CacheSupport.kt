package com.example.distcomp.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class CacheSupport(
    private val redisTemplate: RedisTemplate<String, Any>?,
    @Value("\${distcomp.cache.ttl:10m}") private val ttl: Duration
) {
    private val fallbackStore = ConcurrentHashMap<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrPut(cacheName: String, key: Any, supplier: () -> T): T {
        val redisKey = fullKey(cacheName, key)
        return runCatching {
            val cached = redisTemplate?.opsForValue()?.get(redisKey) ?: fallbackStore[redisKey]
            if (cached != null) {
                return cached as T
            }

            supplier().also { put(cacheName, key, it) }
        }.getOrElse {
            fallbackStore[redisKey] as? T ?: supplier().also { fallbackStore[redisKey] = it }
        }
    }

    fun put(cacheName: String, key: Any, value: Any?) {
        if (value == null) {
            return
        }
        val redisKey = fullKey(cacheName, key)
        fallbackStore[redisKey] = value
        runCatching {
            redisTemplate?.opsForValue()?.set(redisKey, value, ttl)
        }
    }

    fun evict(cacheName: String, key: Any) {
        val redisKey = fullKey(cacheName, key)
        fallbackStore.remove(redisKey)
        runCatching {
            redisTemplate?.delete(redisKey)
        }
    }

    fun clear(cacheName: String) {
        val prefix = "$cacheName::"
        fallbackStore.keys.removeIf { it.startsWith(prefix) }
        runCatching {
            val keys = redisTemplate?.keys("$cacheName::*")
            if (!keys.isNullOrEmpty()) {
                redisTemplate.delete(keys)
            }
        }
    }

    private fun fullKey(cacheName: String, key: Any): String = "$cacheName::$key"
}
