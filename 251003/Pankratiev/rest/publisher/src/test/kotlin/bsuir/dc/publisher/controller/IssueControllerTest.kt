package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.IssueRequestTo
import bsuir.dc.publisher.dto.to.IssueResponseTo
import bsuir.dc.publisher.service.IssueService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(controllers = [IssueController::class])
@Import(IssueControllerTest.TestConfig::class, IssueController::class)
class IssueControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var issueService: IssueService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Configuration
    class TestConfig {
        @Bean
        fun issueService(): IssueService = Mockito.mock(IssueService::class.java)
    }

    @Test
    fun `should create an issue`() {
        val issueRequest = IssueRequestTo(
            id = 0,
            writerId = 1,
            labels = listOf("Bug", "Critical"),
            title = "Sample Issue",
            content = "This is a test issue content."
        )
        val issueResponse = IssueResponseTo(
            id = 1,
            writerId = 1,
            title = "Sample Issue",
            content = "This is a test issue content.",
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )

        `when`(issueService.createIssue(issueRequest)).thenReturn(issueResponse)

        mockMvc.perform(
            post("/api/v1.0/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issueRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.writerId").value(1))
            .andExpect(jsonPath("$.title").value("Sample Issue"))
            .andExpect(jsonPath("$.content").value("This is a test issue content."))
    }

    @Test
    fun `should get issue by id`() {
        val issueResponse = IssueResponseTo(
            id = 1,
            writerId = 1,
            title = "Sample Issue",
            content = "This is a test issue content.",
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )
        `when`(issueService.getIssueById(1L)).thenReturn(issueResponse)

        mockMvc.perform(get("/api/v1.0/issues/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.writerId").value(1))
            .andExpect(jsonPath("$.title").value("Sample Issue"))
            .andExpect(jsonPath("$.content").value("This is a test issue content."))
    }

    @Test
    fun `should get all issues`() {
        val issues = listOf(
            IssueResponseTo(
                id = 1,
                writerId = 1,
                title = "Bug in code",
                content = "This is a bug report.",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ),
            IssueResponseTo(
                id = 2,
                writerId = 2,
                title = "Feature request",
                content = "Please add this feature.",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            )
        )
        `when`(issueService.getAllIssues()).thenReturn(issues)

        mockMvc.perform(get("/api/v1.0/issues"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].title").value("Bug in code"))
            .andExpect(jsonPath("$[1].title").value("Feature request"))
    }

    @Test
    fun `should update an issue`() {
        val issueRequest = IssueRequestTo(
            id = 1,
            writerId = 1,
            labels = listOf("Feature"),
            title = "Updated Issue",
            content = "This is an updated issue content."
        )
        val issueResponse = IssueResponseTo(
            id = 1,
            writerId = 1,
            title = "Updated Issue",
            content = "This is an updated issue content.",
            created = LocalDateTime.now(),
            modified = LocalDateTime.now()
        )

        `when`(issueService.updateIssue(issueRequest.id, issueRequest)).thenReturn(issueResponse)

        mockMvc.perform(
            put("/api/v1.0/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issueRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.writerId").value(1))
            .andExpect(jsonPath("$.title").value("Updated Issue"))
            .andExpect(jsonPath("$.content").value("This is an updated issue content."))
    }

    @Test
    fun `should delete an issue`() {
        doNothing().`when`(issueService).deleteIssue(1L)

        mockMvc.perform(delete("/api/v1.0/issues/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should search issues by filters`() {
        val issues = listOf(
            IssueResponseTo(
                id = 1,
                writerId = 1,
                title = "Bug in code",
                content = "This is a bug report.",
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            )
        )

        `when`(
            issueService.getIssuesByFilters(
                labelNames = listOf("Bug"),
                labelIds = null,
                writerLogin = null,
                title = null,
                content = null
            )
        ).thenReturn(issues)

        mockMvc.perform(get("/api/v1.0/issues/search?labelNames=Bug"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].title").value("Bug in code"))
    }
}
