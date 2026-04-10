package com.example.distcomp.service

import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteCommandService: NoteCommandService
) {
    fun create(request: NoteRequestTo): NoteResponseTo = noteCommandService.create(request)

    fun getById(id: Long): NoteResponseTo = noteCommandService.getById(id)

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> =
        noteCommandService.getAll(page, size, sort)

    fun put(id: Long, request: NoteRequestTo): NoteResponseTo = noteCommandService.put(id, request)

    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo = noteCommandService.patch(id, request)

    fun delete(id: Long) = noteCommandService.delete(id)

    fun getNotesByTweetId(tweetId: Long): List<NoteResponseTo> = noteCommandService.getByTweetId(tweetId)
}
