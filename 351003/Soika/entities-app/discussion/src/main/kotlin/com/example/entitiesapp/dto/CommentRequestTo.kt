package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

data class CommentRequestTo(
    val id: Long? = null,

    @field:NotBlank
    @field:Size(min = 2, max = 2048)
    val content: String,

    @field:NotNull
    val storyId: Long,

    val country: String? = null,
    val state: CommentState = CommentState.PENDING
) : Serializable