package com.example.distcomp.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequestTo(
    @field:NotBlank
    @field:Size(min = 2, max = 64)
    val login: String,
    @field:NotBlank
    @field:Size(min = 8, max = 128)
    val password: String
)
