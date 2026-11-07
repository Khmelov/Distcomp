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
 * Controller tests of the {@code /api/v1.0/issues} resource, including the
 * Issue-Tag many-to-many relation stored through {@code tbl_issue_tag}.
 */
class IssueControllerTest extends AbstractIntegrationTest {

    private static final String URL = ApiPaths.V1 + "/issues";

    @Test
    void createStoresIssueWithTimestampAndTags() throws Exception {
        long creatorId = createCreator();
        String tagPrefix = "tag" + uniqueSuffix();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Title %s","content":"Some content",
                                 "tags":["%sa","%sb"]}
                                """.formatted(creatorId, uniqueSuffix(), tagPrefix, tagPrefix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.modified").isNotEmpty())
                .andExpect(jsonPath("$.tags.length()").value(2));
    }

    @Test
    void createRejectsDuplicateTitle() throws Exception {
        long creatorId = createCreator();
        String title = "Title " + uniqueSuffix();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(issue(creatorId, title, "First content")))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(issue(creatorId, title, "Second content")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("40301"));
    }

    @Test
    void createRejectsUnknownCreator() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(issue(999_999L, "Title " + uniqueSuffix(), "Some content")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRejectsTooShortTitle() throws Exception {
        long creatorId = createCreator();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(issue(creatorId, "x", "Some content")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"));
    }

    @Test
    void readUpdateDeleteRoundTrip() throws Exception {
        long creatorId = createCreator();
        long issueId = createIssue(creatorId, "Title " + uniqueSuffix());

        mockMvc.perform(get(URL + "/" + issueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creatorId").value(creatorId));

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"creatorId":%d,"title":"Updated %s","content":"Updated content"}
                                """.formatted(issueId, creatorId, uniqueSuffix())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(org.hamcrest.Matchers.startsWith("Updated")));

        mockMvc.perform(delete(URL + "/" + issueId)).andExpect(status().isNoContent());
        mockMvc.perform(get(URL + "/" + issueId)).andExpect(status().isNotFound());
    }

    @Test
    void issuesAreFoundByTagNameAndDeletedTagsDisappear() throws Exception {
        long creatorId = createCreator();
        String tagName = "tag" + uniqueSuffix();

        String response = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"creatorId":%d,"title":"Tagged %s","content":"Some content","tags":["%s"]}
                                """.formatted(creatorId, uniqueSuffix(), tagName)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long issueId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).get("id").asLong();

        mockMvc.perform(get(URL).param("tag", tagName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(issueId));

        mockMvc.perform(get(ApiPaths.V1 + "/tags").param("name", tagName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(tagName));

        mockMvc.perform(delete(URL + "/" + issueId)).andExpect(status().isNoContent());

        mockMvc.perform(get(ApiPaths.V1 + "/tags").param("name", tagName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private long createCreator() throws Exception {
        String response = mockMvc.perform(post(ApiPaths.V1 + "/creators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"creator_%s","password":"password123","firstname":"Ivan","lastname":"Petrov"}
                                """.formatted(uniqueSuffix())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }

    private long createIssue(long creatorId, String title) throws Exception {
        String response = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(issue(creatorId, title, "Some content")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }

    private String issue(long creatorId, String title, String content) {
        return """
                {"creatorId":%d,"title":"%s","content":"%s"}
                """.formatted(creatorId, title, content);
    }
}
