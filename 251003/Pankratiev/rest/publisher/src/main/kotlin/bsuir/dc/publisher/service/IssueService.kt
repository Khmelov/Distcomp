package bsuir.dc.publisher.service

import bsuir.dc.publisher.dto.from.IssueRequestTo
import bsuir.dc.publisher.dto.to.IssueResponseTo
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Label
import bsuir.dc.publisher.entity.Writer
import bsuir.dc.publisher.mapper.toEntity
import bsuir.dc.publisher.mapper.toResponse
import bsuir.dc.publisher.repository.IssueRepository
import bsuir.dc.publisher.repository.WriterRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class IssueService(
    private val issueRepository: IssueRepository,
    private val writerRepository: WriterRepository,
) {
    fun createIssue(issueRequestTo: IssueRequestTo): IssueResponseTo {
        val writer = writerRepository.findById(issueRequestTo.writerId).orElseThrow { NoSuchElementException() }
        val labels = issueRequestTo.labels.map { Label(name = it) }.toMutableSet()
        val issue =  issueRequestTo.toEntity(writer, labels)
        val savedIssue = issueRepository.save(issue)
        return savedIssue.toResponse()
    }

    fun getIssueById(id: Long): IssueResponseTo {
        val issue = issueRepository.findById(id).orElseThrow { NoSuchElementException() }
        return issue.toResponse()
    }

    fun getAllIssues(): List<IssueResponseTo> =
        issueRepository.findAll().map { it.toResponse() }

    fun updateIssue(id: Long, issueRequestTo: IssueRequestTo): IssueResponseTo {
        val writer = writerRepository.findById(issueRequestTo.writerId).orElseThrow { NoSuchElementException() }
        val labels = issueRequestTo.labels.map { Label(name = it) }.toMutableSet()
        val updatedIssue = issueRequestTo.toEntity(writer, labels).apply {
            this.id = id
            this.modified = LocalDateTime.now()
        }
        return issueRepository.save(updatedIssue).toResponse()
    }

    fun deleteIssue(id: Long) {
        issueRepository.findById(id).orElseThrow { NoSuchElementException() }
        issueRepository.deleteById(id)
    }

    fun getIssuesByFilters(
        labelNames: List<String>?,
        labelIds: List<Long>?,
        writerLogin: String?,
        title: String?,
        content: String?
    ): List<IssueResponseTo> {
        val issueIdsByNames = getIssueIdsByLabelNames(labelNames)
        val issueIdsByLabelIds = getIssueIdsByLabelIds(labelIds)
        val writerId = getWriterIdByLogin(writerLogin)

        return filterIssues(issueIdsByNames, issueIdsByLabelIds, writerId, title, content)
    }

    private fun getIssueIdsByLabelNames(labelNames: List<String>?): Set<Long>? {
        if (labelNames.isNullOrEmpty()) return null

        val lowerCaseNames = labelNames.map { it.lowercase() }
        return issueRepository
            .findByAllLabelNames(lowerCaseNames, lowerCaseNames.count().toLong())
            .map { it.id }.toSet()
    }

    private fun getIssueIdsByLabelIds(labelIds: List<Long>?): Set<Long>? {
        if (labelIds.isNullOrEmpty()) return null

        return issueRepository
            .findByAllLabelIds(labelIds, labelIds.count().toLong())
            .map { it.id }.toSet()
    }

    private fun getWriterIdByLogin(writerLogin: String?): Long? {
        if (writerLogin.isNullOrBlank()) return null

        val writer = writerRepository.findByLoginIgnoreCase(writerLogin)
        return writer?.id ?: -1
    }

    private fun filterIssues(
        issueIdsByNames: Set<Long>?,
        issueIdsByLabelIds: Set<Long>?,
        writerId: Long?,
        title: String?,
        content: String?
    ): List<IssueResponseTo> {
        val specification = IssueSpecifications.filterByParams(issueIdsByNames, issueIdsByLabelIds, writerId, title, content)
        return issueRepository.findAll(specification).map { it.toResponse() }
    }
}

object IssueSpecifications {
    fun filterByParams(
        issueIdsByNames: Set<Long>?,
        issueIdsByLabelIds: Set<Long>?,
        writerId: Long?,
        title: String?,
        content: String?
    ): Specification<Issue> {
        return Specification { root, query, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            issueIdsByNames?.let {
                predicates.add(root.get<Long>("id").`in`(it))
            }

            issueIdsByLabelIds?.let {
                predicates.add(root.get<Long>("id").`in`(it))
            }

            writerId?.let {
                predicates.add(criteriaBuilder.equal(root.get<Writer>("writer").get<Long>("id"), it))
            }

            title?.let {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%${it.lowercase()}%"))
            }

            content?.let {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%${it.lowercase()}%"))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}
