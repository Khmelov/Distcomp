package com.distcomp.exception

import org.springframework.http.HttpStatus

class NewsTitleDuplicateException (errorMessage: String)
    : AbstractException(HttpStatus.FORBIDDEN, errorMessage)