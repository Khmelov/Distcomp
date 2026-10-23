package com.distcomp.controller;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.service.CreatorService;
import com.distcomp.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests of the {@code /api/v1.0/notices} resource.
 */
@SpringBootTest
@AutoConfigureMockMvc
class NoticeControllerTest {

    private static final String URL = ApiPaths.V1 + "/notices";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreatorService creatorService;

    @Autowired
    private NoticeService noticeService;

    @Test
    void createAndReadNotice() throws Exception {
        long issueId = createIssue();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":" + issueId + ",\"content\":\"Notice content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.issueId").value(issueId));
    }

    @Test
    void createRejectsUnknownIssue() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":999999,\"content\":\"Notice content\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("40401"));
    }

    @Test
    void createRejectsTooShortContent() throws Exception {
        long issueId = createIssue();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":" + issueId + ",\"content\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void updateChangesContent() throws Exception {
        long issueId = createIssue();
        long id = noticeService.create(new NoticeRequestTo(null, issueId, "Initial content")).id();

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"issueId\":" + issueId + ",\"content\":\"Updated content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void deleteRemovesNotice() throws Exception {
        long issueId = createIssue();
        long id = noticeService.create(new NoticeRequestTo(null, issueId, "Initial content")).id();

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void noticesAreFoundByIssueIdentifier() throws Exception {
        long issueId = createIssue();
        noticeService.create(new NoticeRequestTo(null, issueId, "Initial content"));

        mockMvc.perform(get(URL + "/by-issue/" + issueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].issueId").value(issueId));
    }

    private long createIssue() throws Exception {
        long creatorId = creatorService.create(new CreatorRequestTo(
                null, "creator_" + System.nanoTime(), "password123", "Ivan", "Petrov")).id();

        String response = mockMvc.perform(post(ApiPaths.V1 + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Issue title","content":"Issue content"}
                                """.formatted(creatorId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }
}
