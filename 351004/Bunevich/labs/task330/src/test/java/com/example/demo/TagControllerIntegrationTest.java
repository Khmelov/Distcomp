package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class TagControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void tagCrudFlowWorks() throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "java"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/tags/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("java"));

        mockMvc.perform(get("/api/v1.0/tags?page=0&size=10&sortBy=id&sortDir=asc&name=jav"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1.0/tags/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "spring"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("spring"));

        mockMvc.perform(delete("/api/v1.0/tags/{id}", id))
                .andExpect(status().isNoContent());
    }
}
