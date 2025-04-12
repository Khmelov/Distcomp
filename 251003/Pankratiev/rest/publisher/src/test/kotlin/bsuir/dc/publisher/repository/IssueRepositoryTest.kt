package bsuir.dc.publisher.repository

import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Label
import bsuir.dc.publisher.entity.Writer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
class IssueRepositoryTest @Autowired constructor(
    private val issueRepository: IssueRepository,
    private val labelRepository: LabelRepository,
    private val writerRepository: WriterRepository
) {

    @Test
    fun `test findByAllLabelIds`() {
        val writer = writerRepository.save(
            Writer(login = "test_writer")
        )

        val label1 = labelRepository.save(Label(name = "Label1"))
        val label2 = labelRepository.save(Label(name = "Label2"))

        val issue = Issue(
            title = "Test Issue",
            content = "This is a test content",
            writer = writer,
            created = LocalDateTime.now(),
            modified = LocalDateTime.now(),
            labels = mutableSetOf(label1, label2)
        )
        issueRepository.save(issue)

        val result = issueRepository.findByAllLabelIds(
            labelIds = listOf(label1.id, label2.id),
            labelCount = 2
        )

        assertEquals(1, result.size)
        assertEquals(issue.title, result[0].title)
    }

    @Test
    fun `test findByAllLabelNames`() {
        val writer = writerRepository.save(
            Writer(login = "test_writer")
        )

        val label1 = labelRepository.save(Label(name = "LabelA"))
        val label2 = labelRepository.save(Label(name = "LabelB"))

        val issue = Issue(
            title = "Test Issue Name",
            content = "This is another test content",
            writer = writer,
            created = LocalDateTime.now(),
            modified = LocalDateTime.now(),
            labels = mutableSetOf(label1, label2)
        )
        issueRepository.save(issue)

        val result = issueRepository.findByAllLabelNames(
            labelNames = listOf("LabelA", "LabelB"),
            labelCount = 2
        )

        assertEquals(1, result.size)
        assertEquals(issue.title, result[0].title)
    }
}
