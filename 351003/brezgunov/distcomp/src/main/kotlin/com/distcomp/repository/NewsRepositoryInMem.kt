package com.distcomp.repository

import com.distcomp.entity.News
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.set

@Repository
class NewsRepositoryInMem : CrudRepository<News> {
    private val newsMap = ConcurrentHashMap<Long, News>()
    private val counter = AtomicLong(0L)

    override fun save(news: News) {
        val index = if (news.id == null) {
            val newId = counter.incrementAndGet()
            news.id = newId
            newId
        } else {
            news.id!!
        }

        newsMap[index] = news
    }

    override fun findById(id: Long): News? {
        return newsMap[id]
    }

    override fun findAll(): List<News> {
        return newsMap.values.toList()
    }

    override fun removeById(id: Long) {
        newsMap.remove(id)
    }
}