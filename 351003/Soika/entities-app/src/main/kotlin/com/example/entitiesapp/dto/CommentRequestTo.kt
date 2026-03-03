package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CommentRequestTo(

    @field:NotBlank
    val content: String,

    @field:NotNull
    val storyId: Long
)