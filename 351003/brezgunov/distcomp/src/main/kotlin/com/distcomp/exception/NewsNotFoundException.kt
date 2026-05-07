package com.distcomp.exception

import org.springframework.http.HttpStatus

class NewsNotFoundException (
    errorMsg: String
) : AbstractException(HttpStatus.NOT_FOUND, errorMsg)