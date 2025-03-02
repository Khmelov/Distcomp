package bsuir.dc.rest.service

import bsuir.dc.rest.dto.from.WriterFrom
import bsuir.dc.rest.dto.to.WriterTo
import bsuir.dc.rest.mapper.toEntity
import bsuir.dc.rest.mapper.toResponse
import bsuir.dc.rest.repository.memory.IssueInMemoryRepository
import bsuir.dc.rest.repository.memory.WriterInMemoryRepository
import org.springframework.stereotype.Service

@Service
class WriterService(
    private val writerRepository: WriterInMemoryRepository,
    private val issueRepository: IssueInMemoryRepository,
) {
    fun createWriter(writerFrom: WriterFrom): WriterTo {
        val writer = writerFrom.toEntity()
        val savedWriter = writerRepository.save(writer)
        return savedWriter.toResponse()
    }

    fun getWriterById(id: Long): WriterTo {
        val writer = writerRepository.findById(id)
        return writer.toResponse()
    }

    fun getAllWriters(): List<WriterTo> =
        writerRepository.findAll().map { it.toResponse() }

    fun updateWriter(id: Long, writerFrom: WriterFrom): WriterTo {
        val updatedWriter = writerFrom.toEntity().apply { this.id = id }
        return writerRepository.update(updatedWriter).toResponse()
    }

    fun deleteWriter(id: Long) {
        writerRepository.deleteById(id)
    }

    fun getWriterByIssueId(issueId: Long): WriterTo {
        val issue = issueRepository.findById(issueId)
        val writer = writerRepository.findById(issue.writerId)
        return writer.toResponse()
    }
}