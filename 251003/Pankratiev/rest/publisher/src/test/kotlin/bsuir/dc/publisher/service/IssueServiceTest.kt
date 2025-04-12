package bsuir.dc.publisher.service

import bsuir.dc.publisher.dto.from.IssueRequestTo
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Label
import bsuir.dc.publisher.entity.Writer
import bsuir.dc.publisher.repository.IssueRepository
import bsuir.dc.publisher.repository.WriterRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import java.util.*

class IssueServiceTest {

    private val issueRepository = Mockito.mock(IssueRepository::class.java)
    private val writerRepository = Mockito.mock(WriterRepository::class.java)

    private val issueService = IssueService(issueRepository, writerRepository)

    @Test
    fun `should throw exception if writer not found during issue creation`() {
        val issueRequest = IssueRequestTo(
            id = 0,
            writerId = 99,
            labels = listOf("Bug"),
            title = "Sample Issue",
            content = "This is a sample issue content"
        )

        `when`(writerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            issueService.createIssue(issueRequest)
        }
        verify(writerRepository).findById(99L)
    }

    @Test
    fun `should get issue by id`() {
        val issueEntity = Issue(
            id = 1,
            writer = Writer(id = 1, login = "test_user", password = "test123", firstname = "Test", lastname = "User"),
            title = "Sample Issue",
            content = "This is a sample issue content",
            labels = mutableSetOf(Label(name = "Bug")),
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )
        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))

        val result = issueService.getIssueById(1L)

        assertEquals(1L, result.id)
        assertEquals("Sample Issue", result.title)
        assertEquals("This is a sample issue content", result.content)
        verify(issueRepository).findById(1L)
    }

    @Test
    fun `should throw exception when issue not found by id`() {
        `when`(issueRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            issueService.getIssueById(1L)
        }
        verify(issueRepository).findById(1L)
    }

    @Test
    fun `should get all issues`() {
        val issues = listOf(
            Issue(
                id = 1,
                writer = Writer(id = 1, login = "test_user", password = "test123", firstname = "Test", lastname = "User"),
                title = "Issue 1",
                content = "Content 1",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now(),
                labels = mutableSetOf(Label(name = "Bug"))
            ),
            Issue(
                id = 2,
                writer = Writer(id = 2, login = "another_user", password = "test456", firstname = "Another", lastname = "User"),
                title = "Issue 2",
                content = "Content 2",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now(),
                labels = mutableSetOf(Label(name = "Feature"))
            )
        )
        `when`(issueRepository.findAll()).thenReturn(issues)

        val result = issueService.getAllIssues()

        assertEquals(2, result.size)
        assertEquals("Issue 1", result[0].title)
        assertEquals("Issue 2", result[1].title)
        verify(issueRepository).findAll()
    }

    @Test
    fun `should throw exception if writer not found during issue update`() {
        val issueRequest = IssueRequestTo(
            id = 1,
            writerId = 99,
            labels = listOf("Label"),
            title = "Updated Title",
            content = "Updated content"
        )

        `when`(writerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            issueService.updateIssue(1L, issueRequest)
        }
        verify(writerRepository).findById(99L)
    }

    @Test
    fun `should delete issue`() {
        val issueEntity = Issue(
            id = 1,
            writer = Writer(id = 1, login = "test_user", password = "test123", firstname = "Test", lastname = "User"),
            title = "Sample Issue",
            content = "This is a sample issue content",
            labels = mutableSetOf(Label(name = "Bug"))
        )
        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))

        issueService.deleteIssue(1L)

        verify(issueRepository).findById(1L)
        verify(issueRepository).deleteById(1L)
    }

    @Test
    fun `should throw exception when deleting non-existent issue`() {
        `when`(issueRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            issueService.deleteIssue(1L)
        }
        verify(issueRepository).findById(1L)
    }
}
