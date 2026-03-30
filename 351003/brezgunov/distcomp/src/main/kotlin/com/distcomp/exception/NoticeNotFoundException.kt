package com.distcomp.exception

import org.springframework.http.HttpStatus

class NoticeNotFoundException (
    errorMsg: String
) : AbstractException(HttpStatus.NOT_FOUND, errorMsg)