package bsuir.dc.publisher.controller

import bsuir.dc.publisher.dto.from.LabelRequestTo
import bsuir.dc.publisher.dto.to.LabelResponseTo
import bsuir.dc.publisher.service.LabelService
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

@WebMvcTest(controllers = [LabelController::class])
@Import(LabelControllerTest.TestConfig::class, LabelController::class)
class LabelControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var labelService: LabelService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Configuration
    class TestConfig {
        @Bean
        fun labelService(): LabelService = Mockito.mock(LabelService::class.java)
    }

    @Test
    fun `should create a label`() {
        val labelRequest = LabelRequestTo(
            id = 0,
            name = "Bug"
        )
        val labelResponse = LabelResponseTo(
            id = 1,
            name = "Bug"
        )

        `when`(labelService.createLabel(labelRequest)).thenReturn(labelResponse)

        mockMvc.perform(
            post("/api/v1.0/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labelRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Bug"))
    }

    @Test
    fun `should get label by id`() {
        val labelResponse = LabelResponseTo(
            id = 1,
            name = "Bug"
        )
        `when`(labelService.getLabelById(1L)).thenReturn(labelResponse)

        mockMvc.perform(get("/api/v1.0/labels/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Bug"))
    }

    @Test
    fun `should get all labels`() {
        val labels = listOf(
            LabelResponseTo(id = 1, name = "Bug"),
            LabelResponseTo(id = 2, name = "Feature")
        )
        `when`(labelService.getAllLabels()).thenReturn(labels)

        mockMvc.perform(get("/api/v1.0/labels"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].name").value("Bug"))
            .andExpect(jsonPath("$[1].name").value("Feature"))
    }

    @Test
    fun `should update label`() {
        val labelRequest = LabelRequestTo(
            id = 1,
            name = "Updated Bug"
        )
        val labelResponse = LabelResponseTo(
            id = 1,
            name = "Updated Bug"
        )

        `when`(labelService.updateLabel(labelRequest.id, labelRequest)).thenReturn(labelResponse)

        mockMvc.perform(
            put("/api/v1.0/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labelRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Bug"))
    }

    @Test
    fun `should delete label`() {
        doNothing().`when`(labelService).deleteLabel(1L)

        mockMvc.perform(delete("/api/v1.0/labels/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should get labels by issue id`() {
        val labels = listOf(
            LabelResponseTo(id = 1, name = "Bug"),
            LabelResponseTo(id = 2, name = "Critical Bug")
        )

        `when`(labelService.getLabelsByIssueId(1L)).thenReturn(labels)

        mockMvc.perform(get("/api/v1.0/labels/by-issue/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].name").value("Bug"))
            .andExpect(jsonPath("$[1].name").value("Critical Bug"))
    }
}
