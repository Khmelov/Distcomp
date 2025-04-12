package bsuir.dc.publisher.service

import bsuir.dc.publisher.dto.from.WriterRequestTo
import bsuir.dc.publisher.dto.to.WriterResponseTo
import bsuir.dc.publisher.entity.Writer
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.repository.WriterRepository
import bsuir.dc.publisher.repository.IssueRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*

class WriterServiceTest {

    private val writerRepository = Mockito.mock(WriterRepository::class.java)
    private val issueRepository = Mockito.mock(IssueRepository::class.java)

    private val writerService = WriterService(writerRepository, issueRepository)

    @Test
    fun `should create writer`() {
        val writerRequest = WriterRequestTo(
            id = 0,
            login = "test_user",
            password = "password123",
            firstname = "Test",
            lastname = "User"
        )
        val writerEntity = Writer(
            id = 0,
            login = "test_user",
            password = "password123",
            firstname = "Test",
            lastname = "User"
        )
        val savedWriter = writerEntity.copy(id = 1)

        `when`(writerRepository.save(writerEntity)).thenReturn(savedWriter)

        val result = writerService.createWriter(writerRequest)

        assertEquals(1L, result.id)
        assertEquals("test_user", result.login)
        assertEquals("Test", result.firstname)
        assertEquals("User", result.lastname)
        verify(writerRepository).save(writerEntity)
    }

    @Test
    fun `should get writer by id`() {
        val writerEntity = Writer(
            id = 1,
            login = "test_user",
            password = "password123",
            firstname = "Test",
            lastname = "User"
        )
        `when`(writerRepository.findById(1L)).thenReturn(Optional.of(writerEntity))

        val result = writerService.getWriterById(1L)

        assertEquals(1L, result.id)
        assertEquals("test_user", result.login)
        assertEquals("Test", result.firstname)
        assertEquals("User", result.lastname)
        verify(writerRepository).findById(1L)
    }

    @Test
    fun `should throw exception when writer not found by id`() {
        `when`(writerRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            writerService.getWriterById(1L)
        }
        verify(writerRepository).findById(1L)
    }

    @Test
    fun `should get all writers`() {
        val writers = listOf(
            Writer(id = 1, login = "user1", password = "pass1", firstname = "First1", lastname = "Last1"),
            Writer(id = 2, login = "user2", password = "pass2", firstname = "First2", lastname = "Last2")
        )
        `when`(writerRepository.findAll()).thenReturn(writers)

        val result = writerService.getAllWriters()

        assertEquals(2, result.size)
        assertEquals("user1", result[0].login)
        assertEquals("user2", result[1].login)
        verify(writerRepository).findAll()
    }

    @Test
    fun `should update writer`() {
        val writerRequest = WriterRequestTo(
            id = 1,
            login = "updated_user",
            password = "newpass123",
            firstname = "Updated",
            lastname = "User"
        )
        val writerEntity = Writer(
            id = 1,
            login = "updated_user",
            password = "newpass123",
            firstname = "Updated",
            lastname = "User"
        )
        `when`(writerRepository.save(writerEntity)).thenReturn(writerEntity)

        val result = writerService.updateWriter(1L, writerRequest)

        assertEquals(1L, result.id)
        assertEquals("updated_user", result.login)
        assertEquals("Updated", result.firstname)
        assertEquals("User", result.lastname)
        verify(writerRepository).save(writerEntity)
    }

    @Test
    fun `should delete writer`() {
        val writerEntity = Writer(
            id = 1,
            login = "test_user",
            password = "password123",
            firstname = "Test",
            lastname = "User"
        )
        `when`(writerRepository.findById(1L)).thenReturn(Optional.of(writerEntity))

        writerService.deleteWriter(1L)

        verify(writerRepository).findById(1L)
        verify(writerRepository).deleteById(1L)
    }

    @Test
    fun `should throw exception when deleting non-existent writer`() {
        `when`(writerRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            writerService.deleteWriter(1L)
        }
        verify(writerRepository).findById(1L)
    }

    @Test
    fun `should get writer by issue id`() {
        val writerEntity = Writer(
            id = 1,
            login = "test_user",
            password = "password123",
            firstname = "Test",
            lastname = "User"
        )
        val issueEntity = Issue(id = 1, title = "Issue", content = "Some content", writer = writerEntity)
        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))

        val result = writerService.getWriterByIssueId(1L)

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("test_user", result?.login)
        verify(issueRepository).findById(1L)
    }

    @Test
    fun `should throw exception if issue not found when getting writer by issue id`() {
        `when`(issueRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            writerService.getWriterByIssueId(1L)
        }
        verify(issueRepository).findById(1L)
    }
}
