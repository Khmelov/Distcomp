package com.distcomp.exception

import org.springframework.http.HttpStatus

class DuplicateUserException(errorMsg: String) :
    AbstractException(HttpStatus.FORBIDDEN, errorMsg)