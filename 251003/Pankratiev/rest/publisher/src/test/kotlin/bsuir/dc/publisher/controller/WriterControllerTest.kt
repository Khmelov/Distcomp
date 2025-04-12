package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.WriterRequestTo
import bsuir.dc.publisher.dto.to.WriterResponseTo
import bsuir.dc.publisher.service.WriterService
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [WriterController::class])
@Import(WriterControllerTest.TestConfig::class, WriterController::class)
class WriterControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var writerService: WriterService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Configuration
    class TestConfig {
        @Bean
        fun writerService(): WriterService = Mockito.mock(WriterService::class.java)
    }

    @Test
    fun `should create a writer`() {
        val writerRequest = WriterRequestTo(
            id = 0,
            login = "test_user",
            password = "password123",
            firstname = "Test",
            lastname = "User"
        )
        val writerResponse = WriterResponseTo(
            id = 1,
            login = "test_user",
            firstname = "Test",
            lastname = "User"
        )

        `when`(writerService.createWriter(writerRequest)).thenReturn(writerResponse)

        mockMvc.perform(
            post("/api/v1.0/writers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writerRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.login").value("test_user"))
            .andExpect(jsonPath("$.firstname").value("Test"))
            .andExpect(jsonPath("$.lastname").value("User"))
    }

    @Test
    fun `should get writer by id`() {
        val writerResponse = WriterResponseTo(
            id = 1,
            login = "test_user",
            firstname = "Test",
            lastname = "User"
        )
        `when`(writerService.getWriterById(1L)).thenReturn(writerResponse)

        mockMvc.perform(get("/api/v1.0/writers/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.login").value("test_user"))
            .andExpect(jsonPath("$.firstname").value("Test"))
            .andExpect(jsonPath("$.lastname").value("User"))
    }

    @Test
    fun `should get all writers`() {
        val writers = listOf(
            WriterResponseTo(id = 1, login = "user1", firstname = "First", lastname = "User"),
            WriterResponseTo(id = 2, login = "user2", firstname = "Second", lastname = "User")
        )

        `when`(writerService.getAllWriters()).thenReturn(writers)

        mockMvc.perform(get("/api/v1.0/writers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
    }

    @Test
    fun `should update writer`() {
        val writerRequest = WriterRequestTo(
            id = 1,
            login = "updated_user",
            password = "newpass123",
            firstname = "Updated",
            lastname = "User"
        )
        val writerResponse = WriterResponseTo(
            id = 1,
            login = "updated_user",
            firstname = "Updated",
            lastname = "User"
        )

        `when`(writerService.updateWriter(writerRequest.id, writerRequest)).thenReturn(writerResponse)

        mockMvc.perform(
            put("/api/v1.0/writers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writerRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.login").value("updated_user"))
            .andExpect(jsonPath("$.firstname").value("Updated"))
            .andExpect(jsonPath("$.lastname").value("User"))
    }

    @Test
    fun `should delete writer`() {
        doNothing().`when`(writerService).deleteWriter(1L)

        mockMvc.perform(delete("/api/v1.0/writers/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should get writer by issue id`() {
        val writerResponse = WriterResponseTo(
            id = 1,
            login = "issue_writer",
            firstname = "Issue",
            lastname = "Writer"
        )

        `when`(writerService.getWriterByIssueId(1L)).thenReturn(writerResponse)

        mockMvc.perform(get("/api/v1.0/writers/by-issue/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.login").value("issue_writer"))
            .andExpect(jsonPath("$.firstname").value("Issue"))
            .andExpect(jsonPath("$.lastname").value("Writer"))
    }
}
