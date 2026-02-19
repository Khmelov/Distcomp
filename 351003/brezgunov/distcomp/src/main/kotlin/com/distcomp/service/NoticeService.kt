package com.distcomp.service

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.exception.NewsNotFoundException
import com.distcomp.exception.NoticeNotFoundException
import com.distcomp.mapper.NoticeMapper
import com.distcomp.repository.NewsRepository
import com.distcomp.repository.NoticeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NoticeService(
    val noticeMapper: NoticeMapper,
    val noticeRepository: NoticeRepository,
    val newsRepository: NewsRepository
) {
    fun createNotice(noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)
        noticeRepository.save(notice)
        val news = newsRepository.findByIdOrNull(noticeRequestTo.newsId)
        notice.news = news ?: throw NewsNotFoundException("News not found")
        return noticeMapper.toNoticeResponse(notice)
    }

    fun readNoticeById(id: Long): NoticeResponseTo {
        val notice = noticeRepository.findByIdOrNull(id)
            ?: throw NoticeNotFoundException("Notice with id $id not found")
        return noticeMapper.toNoticeResponse(notice)
    }

    fun readAll(): List<NoticeResponseTo> {
        return noticeRepository.findAll().map { noticeMapper.toNoticeResponse(it) }
    }

    fun updateNotice(noticeRequestTo: NoticeRequestTo, noticeId: Long?): NoticeResponseTo {
        if (noticeId == null || noticeRepository.findByIdOrNull(noticeId) == null) {
            throw NoticeNotFoundException("Notice with id $noticeId not found")
        }

        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)
        notice.id = noticeId
        noticeRepository.save(notice)

        val news = newsRepository.findByIdOrNull(noticeRequestTo.newsId)
        notice.news = news ?: throw NewsNotFoundException("News not found")

        return noticeMapper.toNoticeResponse(notice)
    }

    fun removeNoticeById(id: Long) {
        if (noticeRepository.findByIdOrNull(id) == null) {
            throw NoticeNotFoundException("Notice with id $id not found")
        }

        noticeRepository.deleteById(id)
    }
}