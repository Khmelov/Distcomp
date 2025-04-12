package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.IssueRequestTo
import bsuir.dc.publisher.dto.to.IssueResponseTo
import bsuir.dc.publisher.service.IssueService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1.0/issues")
class IssueController(
    private val issueService: IssueService
) {

    @PostMapping
    fun createIssue(@RequestBody @Valid issueRequestTo: IssueRequestTo): ResponseEntity<IssueResponseTo> {
        val createdIssue = issueService.createIssue(issueRequestTo)
        return ResponseEntity(createdIssue, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getIssueById(@PathVariable id: Long): ResponseEntity<IssueResponseTo> {
        val issue = issueService.getIssueById(id)
        return ResponseEntity.ok(issue)
    }

    @GetMapping
    fun getAllIssues(): ResponseEntity<List<IssueResponseTo>> {
        val issues = issueService.getAllIssues()
        return ResponseEntity.ok(issues)
    }

    @PutMapping()
    fun updateIssue(@RequestBody @Valid issueRequestTo: IssueRequestTo): ResponseEntity<IssueResponseTo> {
        val updatedIssue = issueService.updateIssue(issueRequestTo.id, issueRequestTo)
        return ResponseEntity.ok(updatedIssue)
    }

    @DeleteMapping("/{id}")
    fun deleteIssue(@PathVariable id: Long): ResponseEntity<Void> {
        issueService.deleteIssue(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search")
    fun searchIssues(
        @RequestParam(required = false) labelNames: List<String>?,
        @RequestParam(required = false) labelIds: List<Long>?,
        @RequestParam(required = false) writerLogin: String?,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) content: String?
    ): ResponseEntity<List<IssueResponseTo>> {
        val issues = issueService.getIssuesByFilters(labelNames, labelIds, writerLogin, title, content)
        return ResponseEntity.ok(issues)
    }
}