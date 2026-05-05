package com.example.discussion.dto.kafka

import kotlin.String

class KafkaNoticeResponse (
    val id: String = "",
    val data: Any? = null,
    val status: String = "",
    val error: String? = null
)