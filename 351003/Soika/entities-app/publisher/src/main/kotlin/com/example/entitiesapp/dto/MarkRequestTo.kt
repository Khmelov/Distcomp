package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank
import java.io.Serializable

data class MarkRequestTo(
    val id: Long? = null,

    @field:NotBlank
    val name: String
) : Serializable