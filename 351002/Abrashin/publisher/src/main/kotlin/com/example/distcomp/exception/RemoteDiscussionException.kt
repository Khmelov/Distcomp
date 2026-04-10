package com.example.distcomp.exception

import org.springframework.http.HttpStatus

class RemoteDiscussionException(
    val status: HttpStatus,
    val remoteCode: Int?,
    message: String
) : RuntimeException(message)
