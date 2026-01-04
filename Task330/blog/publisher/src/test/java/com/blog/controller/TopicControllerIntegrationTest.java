package com.blog.controller;

import com.blog.dto.request.EditorRequestTo;
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

class TopicControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testEditorId;
    private Long testTagId;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем тестового редактора
        EditorRequestTo editorRequest = new EditorRequestTo();
        editorRequest.setLogin("topic.test@example.com");
        editorRequest.setPassword("password123");
        editorRequest.setFirstname("Topic");
        editorRequest.setLastname("Test");

        String editorResponse = mockMvc.perform(post("/api/v1.0/editors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editorRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        testEditorId = objectMapper.readTree(editorResponse).get("id").asLong();

        // Создаем тестовый тег
        TagRequestTo tagRequest = new TagRequestTo("IntegrationTest");

        String tagResponse = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        testTagId = objectMapper.readTree(tagResponse).get("id").asLong();
    }

    @Test
    void shouldCreateTopic() throws Exception {
        TopicRequestTo topicRequest = createTestTopicRequest();

        mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Тестовая тема")))
                .andExpect(jsonPath("$.content", is("Содержание тестовой темы")))
                .andExpect(jsonPath("$.editorId", is(testEditorId.intValue())))
                .andExpect(jsonPath("$.tagIds", hasSize(1)))
                .andExpect(jsonPath("$.tagIds[0]", is(testTagId.intValue())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.modified", notNullValue()));
    }

    @Test
    void shouldGetTopicById() throws Exception {
        // Создаем тему
        TopicRequestTo topicRequest = createTestTopicRequest();
        String response = mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long topicId = objectMapper.readTree(response).get("id").asLong();

        // Получаем тему по ID
        mockMvc.perform(get("/api/v1.0/topics/{id}", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(topicId.intValue())))
                .andExpect(jsonPath("$.title", is("Тестовая тема")));
    }

    @Test
    void shouldGetTopicsByEditorId() throws Exception {
        // Создаем тему
        TopicRequestTo topicRequest = createTestTopicRequest();
        mockMvc.perform(post("/api/v1.0/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topicRequest)));

        // Получаем темы по ID редактора
        mockMvc.perform(get("/api/v1.0/topics/editor/{editorId}", testEditorId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].editorId", is(testEditorId.intValue())));
    }

    @Test
    void shouldUpdateTopic() throws Exception {
        // Создаем тему
        TopicRequestTo topicRequest = createTestTopicRequest();
        String response = mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long topicId = objectMapper.readTree(response).get("id").asLong();

        // Обновляем тему
        topicRequest.setTitle("Обновленный заголовок");
        topicRequest.setContent("Обновленное содержание темы");

        mockMvc.perform(put("/api/v1.0/topics/{id}", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Обновленный заголовок")))
                .andExpect(jsonPath("$.content", is("Обновленное содержание темы")));
    }

    @Test
    void shouldDeleteTopic() throws Exception {
        // Создаем тему
        TopicRequestTo topicRequest = createTestTopicRequest();
        String response = mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long topicId = objectMapper.readTree(response).get("id").asLong();

        // Удаляем тему
        mockMvc.perform(delete("/api/v1.0/topics/{id}", topicId))
                .andExpect(status().isNoContent());

        // Проверяем, что тема удалена
        mockMvc.perform(get("/api/v1.0/topics/{id}", topicId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenEditorNotFound() throws Exception {
        TopicRequestTo topicRequest = new TopicRequestTo();
        topicRequest.setEditorId(99999L); // Несуществующий редактор
        topicRequest.setTitle("Тема");
        topicRequest.setContent("Содержание темы длиннее 4 символов");

        mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateTopicRequest() throws Exception {
        TopicRequestTo topicRequest = new TopicRequestTo();
        topicRequest.setEditorId(testEditorId);
        topicRequest.setTitle(""); // Пустой заголовок
        topicRequest.setContent("к"); // Слишком короткое содержание

        mockMvc.perform(post("/api/v1.0/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    private TopicRequestTo createTestTopicRequest() {
        TopicRequestTo topicRequest = new TopicRequestTo();
        topicRequest.setEditorId(testEditorId);
        topicRequest.setTitle("Тестовая тема");
        topicRequest.setContent("Содержание тестовой темы");

        Set<Long> tagIds = new HashSet<>();
        tagIds.add(testTagId);
        topicRequest.setTagIds(tagIds);

        return topicRequest;
    }
}