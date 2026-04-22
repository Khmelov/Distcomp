package com.distcomp.dto.news

import com.distcomp.dto.marker.MarkerRequestTo
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class NewsRequestTo (
    val id: Long? = null,
    @field:NotBlank
    @field:Size(min = 3, max = 64)
    val title: String,
    @field:NotBlank
    @field:Size(min = 4, max = 2048)
    val content: String,
    @field:NotNull
    val userId: Long,
    val markers: List<String>?,
)