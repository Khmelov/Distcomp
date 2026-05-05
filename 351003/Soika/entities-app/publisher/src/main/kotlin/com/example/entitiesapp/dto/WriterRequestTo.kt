package com.example.entitiesapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable

data class WriterRequestTo(
    val id: Long? = null,

    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val login: String,

    @field:NotBlank
    @field:Size(min = 8, max = 128)
    val password: String,

    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val firstname: String,

    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val lastname: String,

    val role: Role = Role.CUSTOMER
) : Serializable