package com.distcomp.dto.kafka

import com.distcomp.dto.notice.NoticeRequestTo

data class KafkaNoticeRequest(
    val id: String = "",
    val type: String = "",
    val payload: NoticeRequestTo? = null
)