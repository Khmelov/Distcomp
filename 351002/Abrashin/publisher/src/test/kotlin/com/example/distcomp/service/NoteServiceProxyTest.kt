package com.example.distcomp.service

import com.example.distcomp.client.DiscussionClient
import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class NoteServiceProxyTest {
    private val discussionClient = Mockito.mock(DiscussionClient::class.java)
    private val service = NoteService(discussionClient)

    @Test
    fun `create delegates to discussion client`() {
        val request = NoteRequestTo(tweetId = 11, country = "BY", content = "hello")
        val expected = NoteResponseTo(id = 7, tweetId = 11, country = "BY", content = "hello")
        Mockito.`when`(discussionClient.create(request)).thenReturn(expected)

        val result = service.create(request)

        assertEquals(expected, result)
    }

    @Test
    fun `get by id delegates to discussion client`() {
        val expected = NoteResponseTo(id = 5, tweetId = 11, country = "BY", content = "hello")
        Mockito.`when`(discussionClient.getById(5)).thenReturn(expected)

        val result = service.getById(5)

        assertEquals(expected, result)
    }

    @Test
    fun `get all delegates to discussion client`() {
        val sort = arrayOf("id,desc")
        val expected = listOf(NoteResponseTo(id = 1), NoteResponseTo(id = 2))
        Mockito.`when`(discussionClient.getAll(0, 10, sort)).thenReturn(expected)

        val result = service.getAll(0, 10, sort)

        assertEquals(expected, result)
    }

    @Test
    fun `put delegates to discussion client`() {
        val request = NoteRequestTo(tweetId = 1, content = "new")
        val expected = NoteResponseTo(id = 3, tweetId = 1, content = "new")
        Mockito.`when`(discussionClient.update(3, request)).thenReturn(expected)

        val result = service.put(3, request)

        assertEquals(expected, result)
    }

    @Test
    fun `patch delegates to discussion client`() {
        val request = NoteRequestTo(content = "partial")
        val expected = NoteResponseTo(id = 3, tweetId = 1, content = "partial")
        Mockito.`when`(discussionClient.patch(3, request)).thenReturn(expected)

        val result = service.patch(3, request)

        assertEquals(expected, result)
    }

    @Test
    fun `delete delegates to discussion client`() {
        service.delete(3)

        Mockito.verify(discussionClient).delete(3)
    }

    @Test
    fun `get notes by tweet id delegates to discussion client`() {
        val expected = listOf(NoteResponseTo(id = 7, tweetId = 8))
        Mockito.`when`(discussionClient.getByTweetId(8)).thenReturn(expected)

        val result = service.getNotesByTweetId(8)

        assertEquals(expected, result)
    }
}
