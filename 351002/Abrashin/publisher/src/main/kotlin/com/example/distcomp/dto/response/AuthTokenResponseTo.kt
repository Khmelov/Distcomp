package com.example.distcomp.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthTokenResponseTo(
    @field:JsonProperty("access_token")
    val accessToken: String,
    @field:JsonProperty("token_type")
    val tokenType: String = "Bearer"
)
