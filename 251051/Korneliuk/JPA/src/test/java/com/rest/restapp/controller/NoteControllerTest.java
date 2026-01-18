package com.rest.restapp.controller;

import com.rest.restapp.config.AbstractIntegrationTest;
import com.rest.restapp.dto.request.UserRequest;
import com.rest.restapp.dto.request.IssueRequestTo;
import com.rest.restapp.dto.request.NoteRequestTo;
import com.rest.restapp.dto.response.UserResponseTo;
import com.rest.restapp.dto.response.IssueResponseTo;
import com.rest.restapp.dto.response.NoteResponseTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class NoteControllerTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String AUTHORS_URL = "/api/v1.0/authors";
    private static final String ISSUES_URL = "/api/v1.0/issues";
    private static final String NOTICES_URL = "/api/v1.0/notices";

    @Test
    void createNoticeTest_shouldReturnCreated() {
        // Подготовка: автор → issue
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new UserRequest("noticeuser", "pass12345", "Note", "User"),
                UserResponseTo.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestTo(author.getBody().id(), "Test Issue", "Content", null),
                IssueResponseTo.class);

        // Создаём notice
        var noticeRequest = new NoteRequestTo(issue.getBody().id(), "This is a notice");

        var response = restTemplate.postForEntity(NOTICES_URL, noticeRequest, NoteResponseTo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getNoticeByIdTest_shouldReturnOk() {
        // Подготовка
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new UserRequest("usera", "12345678", "Abob", "Bboba"),
                UserResponseTo.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestTo(author.getBody().id(), "Issue for notice", "Content", null),
                IssueResponseTo.class);
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoteRequestTo(issue.getBody().id(), "Note content"),
                NoteResponseTo.class);
        assertThat(notice.getBody())
                .isNotNull();
        Long noticeId = notice.getBody().id();

        var response = restTemplate.getForEntity(NOTICES_URL + "/" + noticeId, NoteResponseTo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getAllNoticesTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(NOTICES_URL, NoteResponseTo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateNoticeTest_shouldReturnOk() {
        // Подготовка
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new UserRequest("updater", "pass12345", "Updater", "Nbb"),
                UserResponseTo.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestTo(author.getBody().id(), "Update issue", "Content", null),
                IssueResponseTo.class);
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoteRequestTo(issue.getBody().id(), "Old notice"),
                NoteResponseTo.class);
        assertThat(notice.getBody())
                .isNotNull();

        Long noticeId = notice.getBody().id();
        var updateRequest = new NoteRequestTo(issue.getBody().id(), "Updated notice content");
        var putEntity = new HttpEntity<>(updateRequest);

        var response = restTemplate.exchange(
                NOTICES_URL + "/" + noticeId,
                HttpMethod.PUT,
                putEntity,
                NoteResponseTo.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deleteNoticeTest_shouldReturnNoContent() {
        // Подготовка
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new UserRequest("deleter", "12345678", "Del", "Nii"),
                UserResponseTo.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestTo(author.getBody().id(), "Del issue", "Content", null),
                IssueResponseTo.class);
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoteRequestTo(issue.getBody().id(), "To delete"),
                NoteResponseTo.class);
        assertThat(notice.getBody())
                .isNotNull();

        Long noticeId = notice.getBody().id();

        var response = restTemplate.exchange(
                NOTICES_URL + "/" + noticeId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getNoticesByIssueIdTest_shouldReturnOk() {
        // Подготовка: создаём issue и несколько notices
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new UserRequest("finder", "pass12345", "Finder", "Nss"),
                UserResponseTo.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestTo(author.getBody().id(), "Target issue", "Content", null),
                IssueResponseTo.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();
        restTemplate.postForEntity(NOTICES_URL,
                new NoteRequestTo(issueId, "Note 1"),
                NoteResponseTo.class);
        restTemplate.postForEntity(NOTICES_URL,
                new NoteRequestTo(issueId, "Note 2"),
                NoteResponseTo.class);

        var response = restTemplate.getForEntity(NOTICES_URL + "/issue/" + issueId, NoteResponseTo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }
}
