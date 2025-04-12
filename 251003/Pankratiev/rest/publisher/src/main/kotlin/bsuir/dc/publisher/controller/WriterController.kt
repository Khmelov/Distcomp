package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.WriterRequestTo
import bsuir.dc.publisher.dto.to.WriterResponseTo
import bsuir.dc.publisher.service.WriterService
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
    fun createWriter(@RequestBody @Valid writerRequestTo: WriterRequestTo): ResponseEntity<WriterResponseTo> {
        val createdWriter = writerService.createWriter(writerRequestTo)
        return ResponseEntity(createdWriter, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getWriterById(@PathVariable id: Long): ResponseEntity<WriterResponseTo> {
        val writer = writerService.getWriterById(id)
        return ResponseEntity.ok(writer)
    }

    @GetMapping
    fun getAllWriters(): ResponseEntity<List<WriterResponseTo>> {
        val writers = writerService.getAllWriters()
        return ResponseEntity.ok(writers)
    }

    @PutMapping()
    fun updateWriter(@RequestBody @Valid writerRequestTo: WriterRequestTo): ResponseEntity<WriterResponseTo> {
        val updatedWriter = writerService.updateWriter(writerRequestTo.id, writerRequestTo)
        return ResponseEntity.ok(updatedWriter)
    }

    @DeleteMapping("/{id}")
    fun deleteWriter(@PathVariable id: Long): ResponseEntity<Void> {
        writerService.deleteWriter(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-issue/{issueId}")
    fun getWriterByIssueId(@PathVariable issueId: Long): ResponseEntity<WriterResponseTo> {
        val writer = writerService.getWriterByIssueId(issueId)
        return ResponseEntity.ok(writer)
    }
}
