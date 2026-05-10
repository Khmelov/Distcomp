package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

data class StoryRequestTo(
    val id: Long? = null,

    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val title: String,

    @field:NotBlank
    @field:Size(min = 4, max = 2048)
    val content: String,

    @field:NotNull
    val writerId: Long,

    val markIds: Set<Long> = emptySet(),

    val marks: Set<String> = emptySet()
) : Serializable