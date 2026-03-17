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
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeService(
    val noticeMapper: NoticeMapper,
    val noticeRepository: NoticeRepository,
    val newsRepository: NewsRepository
) {
    @Transactional
    fun createNotice(noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)
        val news = newsRepository.findByIdOrNull(noticeRequestTo.newsId)
        notice.news = news ?: throw NewsNotFoundException("News not found")
        noticeRepository.save(notice)
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

    @Transactional
    fun updateNotice(noticeRequestTo: NoticeRequestTo, noticeId: Long?): NoticeResponseTo {
        if (noticeId == null || noticeRepository.findByIdOrNull(noticeId) == null) {
            throw NoticeNotFoundException("Notice with id $noticeId not found")
        }

        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)
        notice.id = noticeId

        val news = newsRepository.findByIdOrNull(noticeRequestTo.newsId)
        notice.news = news ?: throw NewsNotFoundException("News not found")

        noticeRepository.save(notice)
        return noticeMapper.toNoticeResponse(notice)
    }

    @Transactional
    fun removeNoticeById(id: Long) {
        if (noticeRepository.findByIdOrNull(id) == null) {
            throw NoticeNotFoundException("Notice with id $id not found")
        }

        noticeRepository.deleteById(id)
    }
}