package bsuir.dc.publisher.repository

import bsuir.dc.publisher.entity.Label
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class LabelRepositoryTest @Autowired constructor(
    private val labelRepository: LabelRepository
) {

    @Test
    fun `test save and find label`() {
        val label = Label(name = "TestLabel")
        labelRepository.save(label)

        val result = labelRepository.findAll()

        assertEquals(1, result.size)
        assertEquals(label.name, result[0].name)
    }
}