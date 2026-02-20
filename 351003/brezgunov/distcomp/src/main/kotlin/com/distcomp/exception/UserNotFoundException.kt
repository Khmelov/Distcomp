package com.distcomp.exception

import org.springframework.http.HttpStatus

class UserNotFoundException (
    errorMsg: String
) : AbstractException(HttpStatus.NOT_FOUND, errorMsg)