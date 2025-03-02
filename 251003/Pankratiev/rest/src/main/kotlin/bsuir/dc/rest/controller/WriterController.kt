package bsuir.dc.rest.controller

import bsuir.dc.rest.dto.from.WriterFrom
import bsuir.dc.rest.dto.to.WriterTo
import bsuir.dc.rest.service.WriterService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/writers")
class WriterController(
    private val writerService: WriterService
) {

    @PostMapping
    fun createWriter(@RequestBody @Valid writerFrom: WriterFrom): ResponseEntity<WriterTo> {
        val createdWriter = writerService.createWriter(writerFrom)
        return ResponseEntity(createdWriter, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getWriterById(@PathVariable id: Long): ResponseEntity<WriterTo> {
        val writer = writerService.getWriterById(id)
        return ResponseEntity.ok(writer)
    }

    @GetMapping
    fun getAllWriters(): ResponseEntity<List<WriterTo>> {
        val writers = writerService.getAllWriters()
        return ResponseEntity.ok(writers)
    }

    @PutMapping()
    fun updateWriter(@RequestBody @Valid writerFrom: WriterFrom): ResponseEntity<WriterTo> {
        val updatedWriter = writerService.updateWriter(writerFrom.id, writerFrom)
        return ResponseEntity.ok(updatedWriter)
    }

    @DeleteMapping("/{id}")
    fun deleteWriter(@PathVariable id: Long): ResponseEntity<Void> {
        writerService.deleteWriter(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-issue/{issueId}")
    fun getWriterByIssueId(@PathVariable issueId: Long): ResponseEntity<WriterTo> {
        val writer = writerService.getWriterByIssueId(issueId)
        return ResponseEntity.ok(writer)
    }
}
