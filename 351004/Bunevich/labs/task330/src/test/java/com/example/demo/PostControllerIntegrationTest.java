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
class PostControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postCrudFlowWorks() throws Exception {
        Long writerId = createWriter();
        Long storyId = createStory(writerId);

        String createResponse = mockMvc.perform(post("/api/v1.0/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postPayload(storyId, "post-content"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1.0/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("post-content"));

        mockMvc.perform(get("/api/v1.0/posts?page=0&size=10&sortBy=id&sortDir=asc&content=post"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1.0/posts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postPayload(storyId, "post-updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("post-updated"));

        mockMvc.perform(delete("/api/v1.0/posts/{id}", id))
                .andExpect(status().isNoContent());
    }

    private Long createWriter() throws Exception {
        String response = mockMvc.perform(post("/api/v1.0/writers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "login", "post_writer_" + System.nanoTime(),
                                "password", "password123",
                                "firstname", "Post",
                                "lastname", "Writer"
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createStory(Long writerId) throws Exception {
        String response = mockMvc.perform(post("/api/v1.0/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "writerId", writerId,
                                "title", "story-for-post",
                                "content", "story-content"
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Map<String, Object> postPayload(Long storyId, String content) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("storyId", storyId);
        payload.put("content", content);
        return payload;
    }
}
