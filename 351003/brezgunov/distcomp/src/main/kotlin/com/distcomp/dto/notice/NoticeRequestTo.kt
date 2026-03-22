package com.distcomp.dto.notice

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class NoticeRequestTo (
    val id: UUID? = null,

    val country: String? = null,

    @field:NotNull
    var newsId: Long,

    @field:NotBlank
    @field:Size(min = 4, max = 2048)
    val content: String
)