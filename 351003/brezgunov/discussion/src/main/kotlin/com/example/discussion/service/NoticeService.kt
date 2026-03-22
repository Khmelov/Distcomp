package com.example.discussion.service

import com.example.discussion.dto.notice.NoticeRequestTo
import com.example.discussion.dto.notice.NoticeResponseTo
import com.example.discussion.exception.NoticeNotFoundException
import com.example.discussion.mapper.NoticeMapper
import com.example.discussion.repository.NoticeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NoticeService (
    private val noticeMapper: NoticeMapper,
    private val noticeRepository : NoticeRepository,
) {
    fun readNoticeById(id: UUID) : NoticeResponseTo {
        val notice = noticeRepository.findByIdOrNull(id) ?: throw NoticeNotFoundException("User not found")

        return noticeMapper.toNoticeResponse(notice)
    }

    fun createNotice(noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        val notice = noticeMapper.toNoticeEntity(noticeRequestTo)

        noticeRepository.save(notice)

        return noticeMapper.toNoticeResponse(notice)
    }

    fun removeNoticeById(id : UUID) {
        val notice = noticeRepository.findByIdOrNull(id) ?: throw NoticeNotFoundException("User not found")

        noticeRepository.delete(notice)
    }
}