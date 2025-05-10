package org.ex.discussion.dto.request

data class PostRequestDTO(
    val id: Long,
    val country: String?,
    val tweetId: Long,
    val content: String
)
