package bsuir.dc.rest

import bsuir.dc.rest.dto.from.LabelFrom
import bsuir.dc.rest.entity.IssueLabel
import bsuir.dc.rest.repository.memory.IssueLabelInMemoryRepository
import bsuir.dc.rest.service.LabelService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LabelServiceTest {

    @Autowired
    private lateinit var labelService: LabelService

    @Autowired
    private lateinit var issueLabelRepository: IssueLabelInMemoryRepository

    @Test
    fun createLabelTest() {
        val labelFrom = LabelFrom(name = "Test Label")
        val createdLabel = labelService.createLabel(labelFrom)
        assertNotNull(createdLabel.id)
        assertEquals("Test Label", createdLabel.name)
    }

    @Test
    fun getLabelByIdTest() {
        val labelFrom = LabelFrom(name = "Label 1")
        val createdLabel = labelService.createLabel(labelFrom)
        val foundLabel = labelService.getLabelById(createdLabel.id)
        assertEquals(createdLabel.id, foundLabel.id)
        assertEquals("Label 1", foundLabel.name)
    }

    @Test
    fun getAllLabelsTest() {
        labelService.createLabel(LabelFrom(name = "Label 1"))
        labelService.createLabel(LabelFrom(name = "Label 2"))
        val labels = labelService.getAllLabels()
        assertTrue(labels.any { it.name == "Label 1" })
        assertTrue(labels.any { it.name == "Label 2" })
    }

    @Test
    fun updateLabelTest() {
        val labelFrom = LabelFrom(name = "Old Label")
        val createdLabel = labelService.createLabel(labelFrom)
        val updatedLabelFrom = LabelFrom(name = "New Label")
        val updatedLabel = labelService.updateLabel(createdLabel.id, updatedLabelFrom)
        assertEquals(createdLabel.id, updatedLabel.id)
        assertEquals("New Label", updatedLabel.name)
    }

    @Test
    fun deleteLabelTest() {
        val labelFrom = LabelFrom(name = "To Delete")
        val createdLabel = labelService.createLabel(labelFrom)
        labelService.deleteLabel(createdLabel.id)
        assertThrows<NoSuchElementException> {
            labelService.getLabelById(createdLabel.id)
        }
    }

    @Test
    fun getLabelsByIssueIdTest() {
        val labelFrom = LabelFrom(name = "Bug")
        val createdLabel = labelService.createLabel(labelFrom)
        issueLabelRepository.save(IssueLabel(issueId = 1, labelId = createdLabel.id))
        val labels = labelService.getLabelsByIssueId(1)
        assertTrue(labels.any { it.id == createdLabel.id })
    }
}
