package com.example.discussion.exception

import org.springframework.http.HttpStatus

class NoticeNotFoundException (
    errorMsg: String
) : AbstractException(HttpStatus.NOT_FOUND, errorMsg)