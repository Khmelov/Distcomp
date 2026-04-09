package com.example.discussion.controller

import com.example.discussion.dto.response.NoteResponseTo
import com.example.discussion.service.NoteService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1.0/tweets")
class TweetNoteController(private val noteService: NoteService) {
    @GetMapping("/{id}/notes")
    fun getByTweetId(@PathVariable id: Long): List<NoteResponseTo> = noteService.getByTweetId(id)
}
