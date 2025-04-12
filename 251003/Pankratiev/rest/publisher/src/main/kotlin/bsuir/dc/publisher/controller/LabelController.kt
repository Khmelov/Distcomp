package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.LabelRequestTo
import bsuir.dc.publisher.dto.to.LabelResponseTo
import bsuir.dc.publisher.service.LabelService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/labels")
class LabelController(
    private val labelService: LabelService
) {

    @PostMapping
    fun createLabel(@RequestBody @Valid labelRequestTo: LabelRequestTo): ResponseEntity<LabelResponseTo> {
        val createdLabel = labelService.createLabel(labelRequestTo)
        return ResponseEntity(createdLabel, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getLabelById(@PathVariable id: Long): ResponseEntity<LabelResponseTo> {
        val label = labelService.getLabelById(id)
        return ResponseEntity.ok(label)
    }

    @GetMapping
    fun getAllLabels(): ResponseEntity<List<LabelResponseTo>> {
        val labels = labelService.getAllLabels()
        return ResponseEntity.ok(labels)
    }

    @PutMapping()
    fun updateLabel(@RequestBody @Valid labelRequestTo: LabelRequestTo): ResponseEntity<LabelResponseTo> {
        val updatedLabel = labelService.updateLabel(labelRequestTo.id, labelRequestTo)
        return ResponseEntity.ok(updatedLabel)
    }

    @DeleteMapping("/{id}")
    fun deleteLabel(@PathVariable id: Long): ResponseEntity<Void> {
        labelService.deleteLabel(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-issue/{issueId}")
    fun getLabelsByIssueId(@PathVariable issueId: Long): ResponseEntity<List<LabelResponseTo>> {
        val labels = labelService.getLabelsByIssueId(issueId)
        return ResponseEntity.ok(labels)
    }
}
