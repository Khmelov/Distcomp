package com.distcomp.exception

import org.springframework.http.HttpStatus

class ValidationException (
    errorMsg: String
) : AbstractException(HttpStatus.BAD_REQUEST, errorMsg)