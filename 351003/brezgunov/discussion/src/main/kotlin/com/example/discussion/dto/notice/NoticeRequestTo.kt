package com.example.discussion.dto.notice

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class NoticeRequestTo (
    val id: UUID? = null,

    @field:NotNull
    val country: String,

    @field:NotNull
    val newsId: Long,

    @field:NotBlank
    @field:Size(min = 4, max = 2048)
    val content: String
)