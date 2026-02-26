package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class WriterRequestTo(

    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val login: String,

    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val password: String,

    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val firstname: String,

    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val lastname: String
)