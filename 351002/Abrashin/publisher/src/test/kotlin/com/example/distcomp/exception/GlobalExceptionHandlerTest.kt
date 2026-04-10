package com.example.distcomp.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.resource.NoResourceFoundException

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `note timeout maps to 503`() {
        val response = handler.handleNoteTimeout(NoteRequestTimeoutException("Timed out waiting for note response"))

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals(GlobalExceptionHandler.ERR_SERVICE_UNAVAILABLE, response.body?.errorCode)
    }

    @Test
    fun `missing static resource maps to 404`() {
        val response = handler.handleNoResourceFound(NoResourceFoundException(HttpMethod.GET, "/api/v1.0/tweets"))

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(GlobalExceptionHandler.ERR_NOT_FOUND, response.body?.errorCode)
    }
}
