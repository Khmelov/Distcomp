package com.distcomp.controller;

import com.distcomp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests of the {@code /api/v1.0/notices} resource.
 */
class NoticeControllerTest extends AbstractIntegrationTest {

    private static final String URL = ApiPaths.V1 + "/notices";

    @Test
    void createStoresNoticeForExistingIssue() throws Exception {
        long issueId = createIssue();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":" + issueId + ",\"content\":\"Notice content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.issueId").value(issueId));
    }

    @Test
    void createRejectsUnknownIssue() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":999999,\"content\":\"Notice content\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("40401"));
    }

    @Test
    void createRejectsTooShortContent() throws Exception {
        long issueId = createIssue();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":" + issueId + ",\"content\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAndDeleteNotice() throws Exception {
        long issueId = createIssue();
        long id = createNotice(issueId);

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"issueId\":" + issueId
                                + ",\"content\":\"Updated content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));

        mockMvc.perform(delete(URL + "/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get(URL + "/" + id)).andExpect(status().isNotFound());
    }

    private long createIssue() throws Exception {
        String creatorResponse = mockMvc.perform(post(ApiPaths.V1 + "/creators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"creator_%s","password":"password123","firstname":"Ivan","lastname":"Petrov"}
                                """.formatted(uniqueSuffix())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long creatorId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(creatorResponse).get("id").asLong();

        String issueResponse = mockMvc.perform(post(ApiPaths.V1 + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Title %s","content":"Some content"}
                                """.formatted(creatorId, uniqueSuffix())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(issueResponse).get("id").asLong();
    }

    private long createNotice(long issueId) throws Exception {
        String response = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"issueId\":" + issueId + ",\"content\":\"Initial content\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }
}
