package com.distcomp.repository

import com.distcomp.entity.Notice
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class NoticeRepositoryInMem : CrudRepository<Notice> {
    private val noticeMap = ConcurrentHashMap<Long, Notice>()
    private val counter = AtomicLong(0L)

    override fun save(notice: Notice) {
        val index = if (notice.id == null) {
            val newId = counter.incrementAndGet()
            notice.id = newId
            newId
        } else {
            notice.id!!
        }

        noticeMap[index] = notice
    }

    override fun findById(id: Long): Notice? {
        return noticeMap[id]
    }

    override fun findAll(): List<Notice> {
        return noticeMap.values.toList()
    }

    override fun removeById(id: Long) {
        noticeMap.remove(id)
    }
}