package com.task.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.dto.MarkRequestTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MarkRequestTo validMark;

    @BeforeEach
    void setUp() {
        validMark = new MarkRequestTo();
        validMark.setName("technology");
    }

    @Test
    void createMark_shouldReturnCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMark)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("technology"));
    }

    @Test
    void getAllMarks_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1.0/marks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getMarkById_shouldReturnOk_whenExists() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMark)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/marks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("technology"));
    }

    @Test
    void updateMark_shouldReturnOk() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMark)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        validMark.setName("updated-mark");

        mockMvc.perform(put("/api/v1.0/marks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMark)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated-mark"));
    }

    @Test
    void deleteMark_shouldReturnNoContent() throws Exception {
        String createdResponse = mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMark)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1.0/marks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1.0/marks/{id}", id))
                .andExpect(status().is4xxClientError()); // обычно 404 Not Found
    }

    @Test
    void getMarksByTweetId_shouldReturn2xxOr4xx() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1.0/marks/by-tweet/{tweetId}", 1L))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500,
                "Expected 2xx or 4xx, but got server error (5xx). Actual status: " + status);
    }

    @Test
    void createMark_withBlankName_shouldReturnBadRequest() throws Exception {
        MarkRequestTo invalid = new MarkRequestTo();
        invalid.setName("");

        mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMark_withLongName_shouldReturnBadRequest() throws Exception {
        MarkRequestTo invalid = new MarkRequestTo();
        invalid.setName("a".repeat(33)); // больше 32 символов

        mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMark_withNullName_shouldReturnBadRequest() throws Exception {

        String jsonWithNullName = "{\"name\": null}";

        mockMvc.perform(post("/api/v1.0/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullName))
                .andExpect(status().isBadRequest());
    }
}