package bsuir.dc.rest.controller

import bsuir.dc.rest.dto.from.IssueFrom
import bsuir.dc.rest.dto.to.IssueTo
import bsuir.dc.rest.service.IssueService
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
    fun createIssue(@RequestBody @Valid issueFrom: IssueFrom): ResponseEntity<IssueTo> {
        val createdIssue = issueService.createIssue(issueFrom)
        return ResponseEntity(createdIssue, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getIssueById(@PathVariable id: Long): ResponseEntity<IssueTo> {
        val issue = issueService.getIssueById(id)
        return ResponseEntity.ok(issue)
    }

    @GetMapping
    fun getAllIssues(): ResponseEntity<List<IssueTo>> {
        val issues = issueService.getAllIssues()
        return ResponseEntity.ok(issues)
    }

    @PutMapping()
    fun updateIssue(@RequestBody @Valid issueFrom: IssueFrom): ResponseEntity<IssueTo> {
        val updatedIssue = issueService.updateIssue(issueFrom.id, issueFrom)
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
    ): ResponseEntity<List<IssueTo>> {
        val issues = issueService.getIssuesByFilters(labelNames, labelIds, writerLogin, title, content)
        return ResponseEntity.ok(issues)
    }
}