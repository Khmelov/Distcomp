package com.example.entitiesapp.exception

class NotFoundException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)