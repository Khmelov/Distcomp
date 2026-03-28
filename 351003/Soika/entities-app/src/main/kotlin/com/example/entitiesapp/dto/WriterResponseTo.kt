package com.example.entitiesapp.dto

data class WriterResponseTo(
    val id: Long,
    val login: String,
    val password: String,
    val firstname: String,
    val lastname: String
)