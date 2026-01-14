package com.blog.discussion.controller;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MessageControllerTest {

    @Container
    static final CassandraContainer<?> cassandra =
            new CassandraContainer<>("cassandra:4.1")
                    .withExposedPorts(9042);

    @DynamicPropertySource
    static void cassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.contact-points",
                cassandra::getContainerIpAddress);
        registry.add("spring.data.cassandra.port",
                () -> cassandra.getMappedPort(9042));
        registry.add("spring.data.cassandra.local-datacenter",
                () -> "datacenter1");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetMessage() throws Exception {
        // Создаем сообщение
        MessageRequestTo request = new MessageRequestTo();
        request.setTopicId(1L);
        request.setContent("Test message content");

        MvcResult createResult = mockMvc.perform(post("/api/v1.0/messages")
                        .param("country", "test-country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test message content"))
                .andExpect(jsonPath("$.topicId").value(1L))
                .andReturn();

        MessageResponseTo createdMessage = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                MessageResponseTo.class
        );

        // Получаем созданное сообщение
        mockMvc.perform(get("/api/v1.0/messages/{id}", createdMessage.getId())
                        .param("topicId", "1")
                        .param("country", "test-country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdMessage.getId()))
                .andExpect(jsonPath("$.content").value("Test message content"));
    }

    @Test
    void testUpdateMessage() throws Exception {
        // Сначала создаем
        MessageRequestTo createRequest = new MessageRequestTo();
        createRequest.setTopicId(2L);
        createRequest.setContent("Original content");

        MvcResult createResult = mockMvc.perform(post("/api/v1.0/messages")
                        .param("country", "test-country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        MessageResponseTo createdMessage = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                MessageResponseTo.class
        );

        // Затем обновляем
        MessageRequestTo updateRequest = new MessageRequestTo();
        updateRequest.setTopicId(2L);
        updateRequest.setContent("Updated content");

        mockMvc.perform(put("/api/v1.0/messages/{id}", createdMessage.getId())
                        .param("topicId", "2")
                        .param("country", "test-country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void testDeleteMessage() throws Exception {
        // Создаем
        MessageRequestTo request = new MessageRequestTo();
        request.setTopicId(3L);
        request.setContent("To be deleted");

        MvcResult createResult = mockMvc.perform(post("/api/v1.0/messages")
                        .param("country", "test-country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        MessageResponseTo createdMessage = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                MessageResponseTo.class
        );

        // Удаляем
        mockMvc.perform(delete("/api/v1.0/messages/{id}", createdMessage.getId())
                        .param("topicId", "3")
                        .param("country", "test-country"))
                .andExpect(status().isNoContent());

        // Проверяем, что удалено
        mockMvc.perform(get("/api/v1.0/messages/{id}", createdMessage.getId())
                        .param("topicId", "3")
                        .param("country", "test-country"))
                .andExpect(status().isNotFound());
    }
}