package com.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Blog API is running")))
                .andExpect(jsonPath("$.version", is("v1.0")))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    void shouldReturnApiInfo() throws Exception {
        mockMvc.perform(get("/api/v1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints", containsString("/api/v1.0/editors")))
                .andExpect(jsonPath("$.endpoints", containsString("/api/v1.0/topics")))
                .andExpect(jsonPath("$.endpoints", containsString("/api/v1.0/tags")))
                .andExpect(jsonPath("$.endpoints", containsString("/api/v1.0/messages")))
                .andExpect(jsonPath("$.port", is("24110")));
    }
}