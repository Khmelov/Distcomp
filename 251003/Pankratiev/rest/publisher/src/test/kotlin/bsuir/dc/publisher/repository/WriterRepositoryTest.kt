package bsuir.dc.publisher.repository

import bsuir.dc.publisher.entity.Writer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class WriterRepositoryTest @Autowired constructor(
    private val writerRepository: WriterRepository
) {

    @Test
    fun `test findByLoginIgnoreCase`() {
        val writer = Writer(
            login = "TestWriter"
        )
        writerRepository.save(writer)

        val result = writerRepository.findByLoginIgnoreCase("testwriter")

        assertNotNull(result)
    }
}
