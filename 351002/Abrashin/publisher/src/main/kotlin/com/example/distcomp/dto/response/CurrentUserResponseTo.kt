package com.example.distcomp.dto.response

data class CurrentUserResponseTo(
    val id: Long,
    val login: String,
    val role: String,
    val firstName: String?,
    val lastName: String?
)
