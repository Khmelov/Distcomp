package bsuir.dc.rest.service

import bsuir.dc.rest.dto.from.IssueFrom
import bsuir.dc.rest.dto.to.IssueTo
import bsuir.dc.rest.mapper.toEntity
import bsuir.dc.rest.mapper.toResponse
import bsuir.dc.rest.repository.memory.IssueInMemoryRepository
import bsuir.dc.rest.repository.memory.IssueLabelInMemoryRepository
import bsuir.dc.rest.repository.memory.LabelInMemoryRepository
import bsuir.dc.rest.repository.memory.WriterInMemoryRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class IssueService(
    private val issueRepository: IssueInMemoryRepository,
    private val labelRepository: LabelInMemoryRepository,
    private val issueLabelRepository: IssueLabelInMemoryRepository,
    private val writerRepository: WriterInMemoryRepository,
) {
    fun createIssue(issueFrom: IssueFrom): IssueTo {
        val issue = issueFrom.toEntity()
        val savedIssue = issueRepository.save(issue)
        return savedIssue.toResponse()
    }

    fun getIssueById(id: Long): IssueTo {
        val issue = issueRepository.findById(id)
        return issue.toResponse()
    }

    fun getAllIssues(): List<IssueTo> =
        issueRepository.findAll().map { it.toResponse() }

    fun updateIssue(id: Long, issueFrom: IssueFrom): IssueTo {
        val updatedIssue = issueFrom.toEntity().apply {
            this.id = id
            this.modified = LocalDateTime.now()
        }
        return issueRepository.update(updatedIssue).toResponse()
    }

    fun deleteIssue(id: Long) {
        issueRepository.deleteById(id)
    }

    fun getIssuesByFilters(
        labelNames: List<String>?,
        labelIds: List<Long>?,
        writerLogin: String?,
        title: String?,
        content: String?
    ): List<IssueTo> {
        val issueIdsByNames = getIssueIdsByLabelNames(labelNames)
        val issueIdsByLabelIds = getIssueIdsByLabelIds(labelIds)
        val writerId = getWriterIdByLogin(writerLogin)

        return filterIssues(issueIdsByNames, issueIdsByLabelIds, writerId, title, content)
    }

    private fun getIssueIdsByLabelNames(labelNames: List<String>?): Set<Long>? {
        if (labelNames.isNullOrEmpty()) return null

        val labelIdsByNames = labelRepository.findAll()
            .filter { label -> labelNames.any { it.contains(label.name, ignoreCase = true) } }
            .map { it.id }
            .toSet()

        return issueLabelRepository.findAll()
            .filter { it.labelId in labelIdsByNames }
            .map { it.issueId }
            .toSet()
    }

    private fun getIssueIdsByLabelIds(labelIds: List<Long>?): Set<Long>? {
        if (labelIds.isNullOrEmpty()) return null

        return issueLabelRepository.findAll()
            .filter { it.labelId in labelIds }
            .map { it.issueId }
            .toSet()
    }

    private fun getWriterIdByLogin(writerLogin: String?): Long? {
        if (writerLogin.isNullOrBlank()) return null

        val writerId = writerRepository.findAll()
            .firstOrNull { it.login.equals(writerLogin, ignoreCase = true) }
            ?.id
        return writerId ?: -1
    }

    private fun filterIssues(
        issueIdsByNames: Set<Long>?,
        issueIdsByLabelIds: Set<Long>?,
        writerId: Long?,
        title: String?,
        content: String?
    ): List<IssueTo> {
        return issueRepository.findAll()
            .filter { issue ->
                (issueIdsByNames == null || issue.id in issueIdsByNames) &&
                (issueIdsByLabelIds == null || issue.id in issueIdsByLabelIds) &&
                (writerId == null || issue.writerId == writerId) &&
                (title == null || issue.title.contains(title, ignoreCase = true)) &&
                (content == null || issue.content.contains(content, ignoreCase = true))
            }
            .map { it.toResponse() }
    }
}