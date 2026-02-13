package com.distcomp.service

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.entity.News
import com.distcomp.entity.Notice
import com.distcomp.exception.NewsNotFoundException
import com.distcomp.exception.NoticeNotFoundException
import com.distcomp.mapper.NoticeMapper
import com.distcomp.repository.CrudRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    val noticeMapper: NoticeMapper,
    val noticeRepository: CrudRepository<Notice>,
    val newsRepository: CrudRepository<News>
) {
    fun createNotice(noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)
        noticeRepository.save(notice)
        val news = newsRepository.findById(noticeRequestTo.newsId)
        notice.news = news ?: throw NewsNotFoundException("News not found")
        return noticeMapper.toNoticeResponse(notice)
    }

    fun readNoticeById(id: Long): NoticeResponseTo {
        val notice = noticeRepository.findById(id)
            ?: throw NoticeNotFoundException("Notice with id $id not found")
        return noticeMapper.toNoticeResponse(notice)
    }

    fun readAll(): List<NoticeResponseTo> {
        return noticeRepository.findAll().map { noticeMapper.toNoticeResponse(it) }
    }

    fun updateNotice(noticeRequestTo: NoticeRequestTo, noticeId: Long?): NoticeResponseTo {
        if (noticeId == null || noticeRepository.findById(noticeId) == null) {
            throw NoticeNotFoundException("Notice with id $noticeId not found")
        }

        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)
        notice.id = noticeId
        noticeRepository.save(notice)

        val news = newsRepository.findById(noticeRequestTo.newsId)
        notice.news = news ?: throw NewsNotFoundException("News not found")

        return noticeMapper.toNoticeResponse(notice)
    }

    fun removeNoticeById(id: Long) {
        if (noticeRepository.findById(id) == null) {
            throw NoticeNotFoundException("Notice with id $id not found")
        }

        noticeRepository.removeById(id)
    }
}