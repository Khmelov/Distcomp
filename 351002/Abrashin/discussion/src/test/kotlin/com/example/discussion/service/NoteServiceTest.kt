package com.example.discussion.service

import com.example.discussion.dto.request.NoteRequestTo
import com.example.discussion.exception.BadRequestException
import com.example.discussion.exception.NotFoundException
import com.example.discussion.model.NoteEntity
import com.example.discussion.model.NoteKey
import com.example.discussion.repository.NoteRepository
import com.example.notecontracts.NoteState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class NoteServiceTest {
    private val repository = Mockito.mock(NoteRepository::class.java)
    private val moderationService = NoteModerationService("spam,scam")
    private val service = NoteService(repository, moderationService)

    @Test
    fun `create validates tweetId`() {
        assertThrows(BadRequestException::class.java) {
            service.create(NoteRequestTo(content = "ok"))
        }
    }

    @Test
    fun `getById throws when missing`() {
        Mockito.`when`(repository.findByKeyId(9)).thenReturn(emptyList())
        assertThrows(NotFoundException::class.java) {
            service.getById(9)
        }
    }

    @Test
    fun `create stores approved entity`() {
        Mockito.`when`(repository.save(Mockito.any(NoteEntity::class.java))).thenAnswer { it.arguments[0] as NoteEntity }

        val result = service.create(NoteRequestTo(tweetId = 5, country = "BY", content = "hello"))

        assertEquals(5L, result.key.tweetId)
        assertEquals("hello", result.content)
        assertEquals(NoteState.APPROVE, result.state)
    }

    @Test
    fun `create declines stop words`() {
        Mockito.`when`(repository.save(Mockito.any(NoteEntity::class.java))).thenAnswer { it.arguments[0] as NoteEntity }

        val result = service.create(NoteRequestTo(tweetId = 5, country = "BY", content = "contains spam"))

        assertEquals(NoteState.DECLINE, result.state)
    }

    @Test
    fun `getById returns approved note`() {
        Mockito.`when`(repository.findByKeyId(9)).thenReturn(listOf(NoteEntity(NoteKey(4, 9), "BY", "ok", NoteState.APPROVE)))

        val result = service.getById(9)

        assertEquals(9L, result.id)
        assertEquals(4L, result.tweetId)
    }

    @Test
    fun `declined note is hidden from reads`() {
        Mockito.`when`(repository.findByKeyId(9)).thenReturn(listOf(NoteEntity(NoteKey(4, 9), "BY", "spam", NoteState.DECLINE)))

        assertThrows(NotFoundException::class.java) {
            service.getById(9)
        }
    }

    @Test
    fun `getAll pages approved values only`() {
        val n1 = NoteEntity(NoteKey(3, 2), "BY", "b", NoteState.APPROVE)
        val n2 = NoteEntity(NoteKey(3, 1), "BY", "a", NoteState.APPROVE)
        val hidden = NoteEntity(NoteKey(3, 3), "BY", "spam", NoteState.DECLINE)
        Mockito.`when`(repository.findAll()).thenReturn(listOf(n1, n2, hidden))

        val result = service.getAll(0, 10, arrayOf("id", "asc"))

        assertEquals(listOf(1L, 2L), result.map { it.id })
    }

    @Test
    fun `patch updates existing content`() {
        val entity = NoteEntity(NoteKey(3, 7), "BY", "old", NoteState.APPROVE)
        Mockito.`when`(repository.findByKeyId(7)).thenReturn(listOf(entity))
        Mockito.`when`(repository.save(Mockito.any(NoteEntity::class.java))).thenAnswer { it.arguments[0] as NoteEntity }

        val result = service.patch(7, NoteRequestTo(content = "new"))

        assertEquals("new", result.content)
        assertEquals(3L, result.key.tweetId)
    }

    @Test
    fun `put replaces existing record and moves partition`() {
        val entity = NoteEntity(NoteKey(3, 8), "BY", "old", NoteState.APPROVE)
        Mockito.`when`(repository.findByKeyId(8)).thenReturn(listOf(entity))
        Mockito.`when`(repository.save(Mockito.any(NoteEntity::class.java))).thenAnswer { it.arguments[0] as NoteEntity }

        val result = service.put(8, NoteRequestTo(tweetId = 9, country = "RU", content = "new"))

        assertEquals(9L, result.key.tweetId)
        assertEquals("RU", result.country)
        Mockito.verify(repository).delete(entity)
    }

    @Test
    fun `put validates required fields`() {
        Mockito.`when`(repository.findByKeyId(8)).thenReturn(listOf(NoteEntity(NoteKey(3, 8), "BY", "old", NoteState.APPROVE)))
        assertThrows(BadRequestException::class.java) {
            service.put(8, NoteRequestTo(tweetId = null, content = "x"))
        }
    }

    @Test
    fun `getByTweetId returns approved notes only`() {
        Mockito.`when`(repository.findByKeyTweetId(3)).thenReturn(
            listOf(
                NoteEntity(NoteKey(3, 1), "BY", "a", NoteState.APPROVE),
                NoteEntity(NoteKey(3, 2), "BY", "b", NoteState.APPROVE),
                NoteEntity(NoteKey(3, 3), "BY", "spam", NoteState.DECLINE)
            )
        )

        val result = service.getByTweetId(3)

        assertEquals(2, result.size)
    }

    @Test
    fun `delete removes existing note`() {
        val entity = NoteEntity(NoteKey(3, 7), "BY", "old", NoteState.APPROVE)
        Mockito.`when`(repository.findByKeyId(7)).thenReturn(listOf(entity))

        service.delete(7)

        Mockito.verify(repository).delete(entity)
    }
}
