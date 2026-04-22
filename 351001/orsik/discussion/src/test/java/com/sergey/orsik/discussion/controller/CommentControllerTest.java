package com.sergey.orsik.discussion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergey.orsik.discussion.exception.GlobalExceptionHandler;
import com.sergey.orsik.discussion.service.CommentDiscussionService;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentDiscussionService commentDiscussionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CommentController(commentDiscussionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCommentsDelegatesToService() throws Exception {
        when(commentDiscussionService.findAll(0, 20, "id", "asc", 5L, null))
                .thenReturn(List.of(new CommentResponseTo(1L, 5L, "c", Instant.now())));

        mockMvc.perform(get("/api/v1.0/comments").param("tweetId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tweetId").value(5));

        verify(commentDiscussionService).findAll(0, 20, "id", "asc", 5L, null);
    }

    @Test
    void postCommentReturnsCreated() throws Exception {
        CommentRequestTo req = new CommentRequestTo(null, 2L, "ab", null);
        when(commentDiscussionService.create(any())).thenReturn(
                new CommentResponseTo(9L, 2L, "ab", Instant.parse("2024-01-01T00:00:00Z")));

        mockMvc.perform(post("/api/v1.0/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9));
    }

    @Test
    void deleteByTweetReturns204() throws Exception {
        mockMvc.perform(delete("/api/v1.0/comments/by-tweet/77"))
                .andExpect(status().isNoContent());
        verify(commentDiscussionService).deleteAllByTweetId(77L);
    }

    @Test
    void putWithPathDelegates() throws Exception {
        CommentRequestTo req = new CommentRequestTo(null, 2L, "updated", null);
        when(commentDiscussionService.update(eq(3L), any())).thenReturn(
                new CommentResponseTo(3L, 2L, "updated", Instant.now()));

        mockMvc.perform(put("/api/v1.0/comments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }
}
