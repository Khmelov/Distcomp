package com.distcomp.dto.user

data class UserRequestTo(
    val id: Long? = null,
    val login: String,
    val password: String,
    val firstname: String,
    val lastname: String,
)
