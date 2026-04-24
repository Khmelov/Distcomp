package com.distcomp.service

import com.distcomp.client.DiscussionServiceClient
import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.exception.NewsNotFoundException
import com.distcomp.repository.NewsRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val discussionServiceClient: DiscussionServiceClient,
    private val newsRepository: NewsRepository,
) {
    fun createNotice(noticeRequestTo: NoticeRequestTo): NoticeResponseTo =
        if (newsRepository.existsById(noticeRequestTo.newsId)) {
            discussionServiceClient.create(noticeRequestTo)
        } else {
            throw NewsNotFoundException("News not found")
        }

    fun updateNotice(noticeId : Long, noticeRequestTo: NoticeRequestTo): NoticeResponseTo =
        if (newsRepository.existsById(noticeRequestTo.newsId)) {
            discussionServiceClient.update(noticeId, noticeRequestTo)
        } else {
            throw NewsNotFoundException("News not found")
        }
}