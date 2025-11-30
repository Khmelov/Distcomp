package com.rest.restapp.controller;

import com.rest.restapp.config.AbstractIntegrationTest;
import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class IssueControllerTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ISSUES_URL = "/api/v1.0/issues";
    private static final String AUTHORS_URL = "/api/v1.0/authors";

    @Test
    void createIssueTest_shouldReturnCreated() {
        // Предварительно создаём автора
        var authorRequest = new AuthorRequestToDto("issueuser", "pass12345", "Issue", "User");
        var author = restTemplate.postForEntity(AUTHORS_URL, authorRequest, AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        Long authorId = author.getBody().id();

        var issueRequest = new IssueRequestToDto(authorId, "Bug report", "App crashes on startup", null);

        var response = restTemplate.postForEntity(ISSUES_URL, issueRequest, IssueResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getIssueByIdTest_shouldReturnOk() {
        // Создаём автора и issue
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("user2", "12345678", "Test", "Author"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Title", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();

        var response = restTemplate.getForEntity(ISSUES_URL + "/" + issueId, IssueResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getAllIssuesTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(ISSUES_URL, IssueResponseToDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateIssueTest_shouldReturnOk() {
        // Создаём автора и issue
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("updater", "pass12345", "Updater", "Man"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Old Title", "Old Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();
        var updatedRequest = new IssueRequestToDto(author.getBody().id(), "New Title", "New Content", null);
        var putEntity = new HttpEntity<>(updatedRequest);

        var response = restTemplate.exchange(
                ISSUES_URL + "/" + issueId,
                HttpMethod.PUT,
                putEntity,
                IssueResponseToDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deleteIssueTest_shouldReturnNoContent() {
        // Создаём автора и issue
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("deleter", "12345678", "Del", "Author"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "To Delete", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();

        var response = restTemplate.exchange(
                ISSUES_URL + "/" + issueId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getAuthorByIssueIdTest_shouldReturnOk() {
        // Создаём автора и issue
        var authorRequest = new AuthorRequestToDto("linkeduser", "pass12345", "Linked", "User");
        var author = restTemplate.postForEntity(AUTHORS_URL, authorRequest, AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Linked Issue", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();

        var response = restTemplate.getForEntity(ISSUES_URL + "/" + issueId + "/author", AuthorResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(author.getBody().id());
    }
}