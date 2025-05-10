package com.example.discussion;

import com.example.discussion.controller.PostController;
import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import com.example.discussion.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/v1.0/notes";
    private PostResponseTo testNote;
    private PostRequestTo validRequest;

    @BeforeEach
    void setUp() {
        testNote = new PostResponseTo();
        testNote.setContent("Test content");
        testNote.setPostId(1L);
        testNote.setId(1L);
        validRequest = new PostRequestTo();
        validRequest.setContent("Valid content");
        validRequest.setTopicId(1l);
    }

    @Test
    void getAllPosts_ShouldReturn200() throws Exception {
        List<PostResponseTo> notes = Collections.singletonList(testNote);
        Mockito.when(postService.findAll()).thenReturn(notes);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Test content"))
                .andExpect(jsonPath("$[0].newsId").value(1L));
    }

    @Test
    void getPostById_WhenExists_ShouldReturn200() throws Exception {
        Mockito.when(postService.findById(1L)).thenReturn(testNote);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPostById_WhenNotExists_ShouldReturn404() throws Exception {
        Mockito.when(postService.findById(999L)).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPost_WithValidData_ShouldReturn201() throws Exception {
        Mockito.when(postService.save(any(PostRequestTo.class))).thenReturn(testNote);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }


    @Test
    void updatePost_WhenExists_ShouldReturn200() throws Exception {
        PostRequestTo updateRequest = new PostRequestTo();
        updateRequest.setContent("Updated content");
        updateRequest.setTopicId(1L);
        PostResponseTo updatedNote = new PostResponseTo();
        updatedNote.setContent("Updated content");
        updatedNote.setPostId(1L);

        Mockito.when(postService.update(any(PostRequestTo.class))).thenReturn(updatedNote);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void updatePost_WhenNotExists_ShouldReturn404() throws Exception {
        PostRequestTo updateRequest = new PostRequestTo();
        updateRequest.setTopicId(1l);
        updateRequest.setContent("Content");
        Mockito.when(postService.update(any(PostRequestTo.class))).thenReturn(null);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_ShouldReturn204() throws Exception {
        Mockito.doNothing().when(postService).deleteById(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(postService).deleteById(1L);
    }

}