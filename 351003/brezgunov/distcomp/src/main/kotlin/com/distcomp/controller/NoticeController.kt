package com.distcomp.controller

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.service.NoticeService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/{version}/notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    @PostMapping(version = "1.0")
    @ResponseStatus(HttpStatus.CREATED)
    fun createNotice(@RequestBody noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        return noticeService.createNotice(noticeRequestTo)
    }

    @GetMapping("{id}")
    fun readNoticeById(@PathVariable("id") id: Long): NoticeResponseTo {
        return noticeService.readNoticeById(id)
    }

    @GetMapping
    fun findAll(): List<NoticeResponseTo> {
        return noticeService.readAll()
    }

    @PutMapping(path = ["/{id}", ""], version = "1.0")
    @ResponseStatus(HttpStatus.OK)
    fun updateNotice(
        @RequestBody noticeRequestTo: NoticeRequestTo,
        @PathVariable("id") id: Long?
    ): NoticeResponseTo {
        return noticeService.updateNotice(noticeRequestTo, id)
    }

    @DeleteMapping(path = ["/{id}"], version = "1.0")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteNotice(@PathVariable("id") id: Long) {
        noticeService.removeNoticeById(id)
    }
}