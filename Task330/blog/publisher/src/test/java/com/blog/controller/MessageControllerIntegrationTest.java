package com.blog.controller;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.request.TagRequestTo;
import com.blog.dto.request.TopicRequestTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MessageControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testTopicId;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем тестового редактора
        EditorRequestTo editorRequest = new EditorRequestTo();
        editorRequest.setLogin("message.test@example.com");
        editorRequest.setPassword("password123");
        editorRequest.setFirstname("Message");
        editorRequest.setLastname("Test");

        String editorResponse = mockMvc.perform(post("/api/v1.0/editors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editorRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long editorId = objectMapper.readTree(editorResponse).get("id").asLong();

        // Создаем тестовый тег
        TagRequestTo tagRequest = new TagRequestTo("MessageTest");

        String tagResponse = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tagId = objectMapper.readTree(tagResponse).get("id").asLong();

        // Создаем тестовую тему
        TopicRequestTo topicRequest = new TopicRequestTo();
        topicRequest.setEditorId(editorId);
        topicRequest.setTitle("Тема для сообщений");
        topicRequest.setContent("Содержание темы для тестирования сообщений");

        Set<Long> tagIds = new HashSet<>();
        tagIds.add(tagId);
        topicRequest.setTagIds(tagIds);

        String topicResponse = mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        testTopicId = objectMapper.readTree(topicResponse).get("id").asLong();
    }

    @Test
    void shouldCreateMessage() throws Exception {
        MessageRequestTo messageRequest = createTestMessageRequest();

        mockMvc.perform(post("/api/v1.0/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.topicId", is(testTopicId.intValue())))
                .andExpect(jsonPath("$.content", is("Тестовое сообщение")));
    }

    @Test
    void shouldGetAllMessages() throws Exception {
        // Сначала создаем сообщение
        MessageRequestTo messageRequest = createTestMessageRequest();
        mockMvc.perform(post("/api/v1.0/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)));

        // Затем получаем все сообщения
        mockMvc.perform(get("/api/v1.0/messages")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].content", notNullValue()));
    }

    @Test
    void shouldGetMessageById() throws Exception {
        // Создаем сообщение
        MessageRequestTo messageRequest = createTestMessageRequest();
        String response = mockMvc.perform(post("/api/v1.0/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long messageId = objectMapper.readTree(response).get("id").asLong();

        // Получаем сообщение по ID
        mockMvc.perform(get("/api/v1.0/messages/{id}", messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(messageId.intValue())))
                .andExpect(jsonPath("$.content", is("Тестовое сообщение")));
    }

    @Test
    void shouldGetMessagesByTopicId() throws Exception {
        // Создаем сообщение
        MessageRequestTo messageRequest = createTestMessageRequest();
        mockMvc.perform(post("/api/v1.0/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)));

        // Получаем сообщения по ID темы
        mockMvc.perform(get("/api/v1.0/messages/topic/{topicId}", testTopicId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].topicId", is(testTopicId.intValue())));
    }

    @Test
    void shouldUpdateMessage() throws Exception {
        // Создаем сообщение
        MessageRequestTo messageRequest = createTestMessageRequest();
        String response = mockMvc.perform(post("/api/v1.0/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long messageId = objectMapper.readTree(response).get("id").asLong();

        // Обновляем сообщение
        messageRequest.setContent("Обновленное сообщение");

        mockMvc.perform(put("/api/v1.0/messages/{id}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Обновленное сообщение")));
    }

    @Test
    void shouldDeleteMessage() throws Exception {
        // Создаем сообщение
        MessageRequestTo messageRequest = createTestMessageRequest();
        String response = mockMvc.perform(post("/api/v1.0/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long messageId = objectMapper.readTree(response).get("id").asLong();

        // Удаляем сообщение
        mockMvc.perform(delete("/api/v1.0/messages/{id}", messageId))
                .andExpect(status().isNoContent());

        // Проверяем, что сообщение удалено
        mockMvc.perform(get("/api/v1.0/messages/{id}", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTopicNotFound() throws Exception {
        MessageRequestTo messageRequest = new MessageRequestTo();
        messageRequest.setTopicId(99999L); // Несуществующая тема
        messageRequest.setContent("Сообщение");

        mockMvc.perform(post("/api/v1.0/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateMessageRequest() throws Exception {
        MessageRequestTo messageRequest = new MessageRequestTo();
        messageRequest.setTopicId(testTopicId);
        messageRequest.setContent(""); // Пустое содержание

        mockMvc.perform(post("/api/v1.0/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    private MessageRequestTo createTestMessageRequest() {
        MessageRequestTo messageRequest = new MessageRequestTo();
        messageRequest.setTopicId(testTopicId);
        messageRequest.setContent("Тестовое сообщение");
        return messageRequest;
    }
}