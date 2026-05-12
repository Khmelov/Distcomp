package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class WriterControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void writerCrudFlowWorks() throws Exception {
        String body = objectMapper.writeValueAsString(writerPayload("writer_login"));

        String createResponse = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/writers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("writer_login"));

        mockMvc.perform(get("/api/v1.0/writers?page=0&size=5&sortBy=id&sortDir=asc&login=writer"))
                .andExpect(status().isOk());

        String updateBody = objectMapper.writeValueAsString(writerPayload("writer_login_updated"));
        mockMvc.perform(put("/api/v1.0/writers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("writer_login_updated"));

        mockMvc.perform(delete("/api/v1.0/writers/{id}", id))
                .andExpect(status().isNoContent());
    }

    private Map<String, Object> writerPayload(String login) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("login", login);
        payload.put("password", "password123");
        payload.put("firstname", "Test");
        payload.put("lastname", "Writer");
        return payload;
    }
}
