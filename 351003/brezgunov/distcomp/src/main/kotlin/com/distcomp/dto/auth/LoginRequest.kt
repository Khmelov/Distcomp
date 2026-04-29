package com.distcomp.dto.auth

data class LoginRequest(
    val login: String,
    val password: String
)