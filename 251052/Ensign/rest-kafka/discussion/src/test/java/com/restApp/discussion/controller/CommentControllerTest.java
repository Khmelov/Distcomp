package com.restApp.discussion.controller;

import com.restApp.discussion.dto.CommentRequestTo;
import com.restApp.discussion.dto.CommentResponseTo;
import com.restApp.discussion.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    void shouldReturnCommentsByNewsId() throws Exception {
        CommentResponseTo comment = new CommentResponseTo(1L, 100L, "Test Content", "US",
                com.restApp.discussion.model.CommentState.APPROVE);
        given(commentService.getCommentsByNewsId(100L)).willReturn(List.of(comment));

        mockMvc.perform(get("/api/v1.0/comments/news/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].country").value("US"));
    }

    @Test
    void shouldCreateComment() throws Exception {
        CommentResponseTo created = new CommentResponseTo(1L, 100L, "New Comment", "US",
                com.restApp.discussion.model.CommentState.APPROVE);
        given(commentService.create(any(CommentRequestTo.class))).willReturn(created);

        mockMvc.perform(post("/api/v1.0/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newsId\": 100, \"content\": \"New Comment\", \"country\": \"US\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
