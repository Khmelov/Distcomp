package com.distcomp.dto.auth

data class LoginResponse(
    val access_token: String,
    val tokenType: String = "Bearer"
)