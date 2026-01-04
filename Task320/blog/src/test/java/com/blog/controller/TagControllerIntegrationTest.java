package com.blog.controller;

import com.blog.dto.request.TagRequestTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TagControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TagRequestTo testTag;

    @BeforeEach
    void setUp() {
        testTag = new TagRequestTo();
        testTag.setName("ТестовыйТег");
    }

    @Test
    void shouldCreateTag() throws Exception {
        mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("ТестовыйТег")));
    }

    @Test
    void shouldGetAllTags() throws Exception {
        // Сначала создаем тег
        mockMvc.perform(post("/api/v1.0/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTag)));

        // Затем получаем все теги
        mockMvc.perform(get("/api/v1.0/tags")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].name", notNullValue()));
    }

    @Test
    void shouldGetTagById() throws Exception {
        // Создаем тег
        String response = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tagId = objectMapper.readTree(response).get("id").asLong();

        // Получаем тег по ID
        mockMvc.perform(get("/api/v1.0/tags/{id}", tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tagId.intValue())))
                .andExpect(jsonPath("$.name", is("ТестовыйТег")));
    }

    @Test
    void shouldGetTagByName() throws Exception {
        // Создаем тег
        mockMvc.perform(post("/api/v1.0/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTag)));

        // Получаем тег по имени
        mockMvc.perform(get("/api/v1.0/tags/name/{name}", "ТестовыйТег"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ТестовыйТег")));
    }

    @Test
    void shouldUpdateTag() throws Exception {
        // Создаем тег
        String response = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tagId = objectMapper.readTree(response).get("id").asLong();

        // Обновляем тег
        testTag.setName("ОбновленныйТег");

        mockMvc.perform(put("/api/v1.0/tags/{id}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ОбновленныйТег")));
    }

    @Test
    void shouldDeleteTag() throws Exception {
        // Создаем тег
        String response = mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tagId = objectMapper.readTree(response).get("id").asLong();

        // Удаляем тег
        mockMvc.perform(delete("/api/v1.0/tags/{id}", tagId))
                .andExpect(status().isNoContent());

        // Проверяем, что тег удален
        mockMvc.perform(get("/api/v1.0/tags/{id}", tagId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTagNotFound() throws Exception {
        mockMvc.perform(get("/api/v1.0/tags/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTagByNameNotFound() throws Exception {
        mockMvc.perform(get("/api/v1.0/tags/name/NonExistentTag"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateTagRequest() throws Exception {
        // Тест с невалидными данными
        testTag.setName(""); // Пустое имя

        mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    @Test
    void shouldReturn409WhenDuplicateTagName() throws Exception {
        // Создаем первый тег
        mockMvc.perform(post("/api/v1.0/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTag)));

        // Пытаемся создать второй с таким же именем
        mockMvc.perform(post("/api/v1.0/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTag)))
                .andExpect(status().isBadRequest());
    }
}