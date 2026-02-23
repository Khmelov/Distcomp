package com.example.entitiesapp.exception

class ValidationException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)