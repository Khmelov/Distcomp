package bsuir.dc.publisher.service

import bsuir.dc.publisher.dto.from.LabelRequestTo
import bsuir.dc.publisher.dto.to.LabelResponseTo
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Label
import bsuir.dc.publisher.repository.LabelRepository
import bsuir.dc.publisher.repository.IssueRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*

class LabelServiceTest {

    private val labelRepository = Mockito.mock(LabelRepository::class.java)
    private val issueRepository = Mockito.mock(IssueRepository::class.java)

    private val labelService = LabelService(labelRepository, issueRepository)

    @Test
    fun `should create label`() {
        val labelRequest = LabelRequestTo(
            id = 0,
            name = "Bug"
        )
        val labelEntity = Label(
            id = 0,
            name = "Bug"
        )
        val savedLabel = labelEntity.copy(id = 1)

        `when`(labelRepository.save(labelEntity)).thenReturn(savedLabel)

        val result = labelService.createLabel(labelRequest)

        assertEquals(1L, result.id)
        assertEquals("Bug", result.name)
        verify(labelRepository).save(labelEntity)
    }

    @Test
    fun `should get label by id`() {
        val labelEntity = Label(
            id = 1,
            name = "Bug"
        )
        `when`(labelRepository.findById(1L)).thenReturn(Optional.of(labelEntity))

        val result = labelService.getLabelById(1L)

        assertEquals(1L, result.id)
        assertEquals("Bug", result.name)
        verify(labelRepository).findById(1L)
    }

    @Test
    fun `should throw exception when label not found by id`() {
        `when`(labelRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            labelService.getLabelById(1L)
        }
        verify(labelRepository).findById(1L)
    }

    @Test
    fun `should get all labels`() {
        val labels = listOf(
            Label(id = 1, name = "Bug"),
            Label(id = 2, name = "Feature")
        )
        `when`(labelRepository.findAll()).thenReturn(labels)

        val result = labelService.getAllLabels()

        assertEquals(2, result.size)
        assertEquals("Bug", result[0].name)
        assertEquals("Feature", result[1].name)
        verify(labelRepository).findAll()
    }

    @Test
    fun `should update label`() {
        val labelRequest = LabelRequestTo(
            id = 1,
            name = "Critical Bug"
        )
        val labelEntity = Label(
            id = 1,
            name = "Critical Bug"
        )
        `when`(labelRepository.save(labelEntity)).thenReturn(labelEntity)

        val result = labelService.updateLabel(1L, labelRequest)

        assertEquals(1L, result.id)
        assertEquals("Critical Bug", result.name)
        verify(labelRepository).save(labelEntity)
    }

    @Test
    fun `should delete label`() {
        val labelEntity = Label(
            id = 1,
            name = "Bug"
        )
        `when`(labelRepository.findById(1L)).thenReturn(Optional.of(labelEntity))

        labelService.deleteLabel(1L)

        verify(labelRepository).findById(1L)
        verify(labelRepository).deleteById(1L)
    }

    @Test
    fun `should throw exception when deleting non-existent label`() {
        `when`(labelRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            labelService.deleteLabel(1L)
        }
        verify(labelRepository).findById(1L)
    }

    @Test
    fun `should get labels by issue id`() {
        val labels = mutableSetOf(
            Label(id = 1, name = "Bug"),
            Label(id = 2, name = "Critical")
        )
        val issueEntity = Issue(id = 1, title = "Issue", content = "Some content", labels = labels)
        `when`(issueRepository.findById(1L)).thenReturn(Optional.of(issueEntity))

        val result = labelService.getLabelsByIssueId(1L)

        assertEquals(2, result.size)
        assertEquals("Bug", result[0].name)
        assertEquals("Critical", result[1].name)
        verify(issueRepository).findById(1L)
    }

    @Test
    fun `should throw exception if issue not found when getting labels by issue id`() {
        `when`(issueRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            labelService.getLabelsByIssueId(1L)
        }
        verify(issueRepository).findById(1L)
    }
}
