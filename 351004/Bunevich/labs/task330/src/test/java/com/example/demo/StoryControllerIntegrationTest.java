package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class StoryControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void storyCrudFlowWorks() throws Exception {
        Long writerId = createWriter();
        Long tagId = createTag();

        String createResponse = mockMvc.perform(post("/api/v1.0/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storyPayload(writerId, "story-title", List.of(tagId)))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/stories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("story-title"));

        mockMvc.perform(get("/api/v1.0/stories?page=0&size=10&sortBy=id&sortDir=asc&title=story"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1.0/stories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storyPayload(writerId, "story-updated", List.of(tagId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("story-updated"));

        mockMvc.perform(delete("/api/v1.0/stories/{id}", id))
                .andExpect(status().isNoContent());
    }

    private Long createWriter() throws Exception {
        String response = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "login", "story_writer_" + System.nanoTime(),
                                "password", "password123",
                                "firstname", "Story",
                                "lastname", "Writer"
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("id").asLong();
    }

    private Long createTag() throws Exception {
        String response = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "tag_" + System.nanoTime()))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Map<String, Object> storyPayload(Long writerId, String title, List<Long> tagIds) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("writerId", writerId);
        payload.put("title", title);
        payload.put("content", "story-content");
        payload.put("tagIds", tagIds);
        return payload;
    }
}
