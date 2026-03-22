package com.distcomp.controller

import com.distcomp.client.DiscussionServiceClient
import com.distcomp.dto.notice.NoticeRequestTo
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{version}/notices")
class NoticeController (
    private val discussionServiceClient: DiscussionServiceClient,
) {
    @GetMapping("{id}")
    fun getById(@PathVariable("id") id: Long) = discussionServiceClient.getById(id)

    @GetMapping
    fun getAll() = discussionServiceClient.getAll()

    @PostMapping
    fun post(@RequestBody request: NoticeRequestTo) = discussionServiceClient.create(request)

    @DeleteMapping("{id}")
    fun removeById(@PathVariable id: Long) = discussionServiceClient.delete(id)
}