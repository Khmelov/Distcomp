package com.example.discussion.exception

import org.springframework.http.HttpStatus

abstract class AbstractException (
    val errorCode: HttpStatus,
    val errorMsg: String
) : RuntimeException(errorMsg)