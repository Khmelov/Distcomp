package com.example.discussion.dto.notice

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class NoticeRequestTo (
    val id: Long? = null,

    @field:NotNull
    val newsId: Long,

    @field:NotBlank
    @field:Size(min = 4, max = 2048)
    val content: String
)