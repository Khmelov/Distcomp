package com.distcomp.controller

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.service.NoticeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{version}/notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    @GetMapping("{id}")
    fun getById(@PathVariable("id") id: Long) = noticeService.getById(id)

    @GetMapping
    fun getAll() = noticeService.getAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody request: NoticeRequestTo) = noticeService.createNotice(request)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateNotice(
        @PathVariable("id") noticeId: Long,
        @Valid @RequestBody noticeRequestTo: NoticeRequestTo
    ) = noticeService.updateNotice(noticeId, noticeRequestTo)

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeById(@PathVariable id: Long) = noticeService.deleteById(id)
}