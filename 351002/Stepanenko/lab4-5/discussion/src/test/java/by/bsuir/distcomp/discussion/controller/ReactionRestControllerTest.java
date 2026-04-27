package by.bsuir.distcomp.discussion.controller;

import by.bsuir.distcomp.discussion.service.ReactionService;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReactionRestController.class)
class ReactionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReactionService reactionService;

    @Test
    void postCreates() throws Exception {
        ReactionRequestTo req = new ReactionRequestTo();
        req.setTweetId(1L);
        req.setContent("ab");
        when(reactionService.create(any())).thenReturn(new ReactionResponseTo(5L, 1L, "ab"));

        mockMvc.perform(post("/api/v1.0/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.tweetId").value(1));
    }

    @Test
    void getByTweetId_returnsList() throws Exception {
        when(reactionService.getByTweetId(3L)).thenReturn(List.of(new ReactionResponseTo(1L, 3L, "x")));

        mockMvc.perform(get("/api/v1.0/reactions/tweet/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tweetId").value(3));
    }

    @Test
    void deleteByTweetId_callsService() throws Exception {
        mockMvc.perform(delete("/api/v1.0/reactions/tweet/9"))
                .andExpect(status().isNoContent());
        verify(reactionService).deleteByTweetId(9L);
    }
}
