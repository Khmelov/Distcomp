package com.example.discussion.dto.kafka

import com.example.discussion.dto.notice.NoticeRequestTo

data class KafkaNoticeRequest(
    val id: String = "",
    val type: String = "",
    val payload: NoticeRequestTo? = null
)