package com.example.discussion.controller

import com.example.discussion.dto.ErrorResponseTo
import com.example.discussion.dto.notice.NoticeRequestTo
import com.example.discussion.dto.notice.NoticeResponseTo
import com.example.discussion.exception.AbstractException
import com.example.discussion.service.NoticeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/{version}/notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    @ExceptionHandler(AbstractException::class)
    fun handleUserNotFound(e: AbstractException): ResponseEntity<ErrorResponseTo> {
        val error = ErrorResponseTo(
            status = e.errorCode.value(),
            message = e.errorMsg
        )
        return ResponseEntity.status(e.errorCode).body(error)
    }

    @PostMapping(version = "1.0")
    @ResponseStatus(HttpStatus.CREATED)
    fun createNotice(@Valid @RequestBody noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        return noticeService.createNotice(noticeRequestTo)
    }

    @GetMapping(path = ["/{id}"], version = "1.0")
    fun readNoticeById(@PathVariable id: UUID): NoticeResponseTo {
        return noticeService.readNoticeById(id)
    }

    @DeleteMapping(path = ["/{id}"], version = "1.0")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteNotice(@PathVariable id: UUID) {
        noticeService.removeNoticeById(id)
    }
}