package com.distcomp.exception

import org.springframework.http.HttpStatus

class NewsForbiddenException (
    errorMsg: String
) : AbstractException(HttpStatus.FORBIDDEN, errorMsg)