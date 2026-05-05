package com.example.entitiesapp.exception

data class ApiError(
    val errorMessage: String,
    val errorCode: Int
)