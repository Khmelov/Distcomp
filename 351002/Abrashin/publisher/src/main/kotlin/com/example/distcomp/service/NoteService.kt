package com.example.distcomp.service

import com.example.distcomp.client.DiscussionClient
import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val discussionClient: DiscussionClient
) {
    fun create(request: NoteRequestTo): NoteResponseTo = discussionClient.create(request)

    fun getById(id: Long): NoteResponseTo = discussionClient.getById(id)

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> =
        discussionClient.getAll(page, size, sort)

    fun put(id: Long, request: NoteRequestTo): NoteResponseTo = discussionClient.update(id, request)

    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo = discussionClient.patch(id, request)

    fun delete(id: Long) = discussionClient.delete(id)

    fun getNotesByTweetId(tweetId: Long): List<NoteResponseTo> = discussionClient.getByTweetId(tweetId)
}