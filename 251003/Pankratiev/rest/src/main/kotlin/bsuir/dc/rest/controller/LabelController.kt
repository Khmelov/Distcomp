package bsuir.dc.rest.controller

import bsuir.dc.rest.dto.from.LabelFrom
import bsuir.dc.rest.dto.to.LabelTo
import bsuir.dc.rest.service.LabelService
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
    fun createLabel(@RequestBody @Valid labelFrom: LabelFrom): ResponseEntity<LabelTo> {
        val createdLabel = labelService.createLabel(labelFrom)
        return ResponseEntity(createdLabel, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getLabelById(@PathVariable id: Long): ResponseEntity<LabelTo> {
        val label = labelService.getLabelById(id)
        return ResponseEntity.ok(label)
    }

    @GetMapping
    fun getAllLabels(): ResponseEntity<List<LabelTo>> {
        val labels = labelService.getAllLabels()
        return ResponseEntity.ok(labels)
    }

    @PutMapping()
    fun updateLabel(@RequestBody @Valid labelFrom: LabelFrom): ResponseEntity<LabelTo> {
        val updatedLabel = labelService.updateLabel(labelFrom.id, labelFrom)
        return ResponseEntity.ok(updatedLabel)
    }

    @DeleteMapping("/{id}")
    fun deleteLabel(@PathVariable id: Long): ResponseEntity<Void> {
        labelService.deleteLabel(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-issue/{issueId}")
    fun getLabelsByIssueId(@PathVariable issueId: Long): ResponseEntity<List<LabelTo>> {
        val labels = labelService.getLabelsByIssueId(issueId)
        return ResponseEntity.ok(labels)
    }
}
