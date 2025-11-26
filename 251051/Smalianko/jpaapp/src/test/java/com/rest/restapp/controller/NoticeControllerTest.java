package com.rest.restapp.controller;

import com.rest.restapp.config.AbstractIntegrationTest;
import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.request.NoticeRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import com.rest.restapp.dto.response.NoticeResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeControllerTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String AUTHORS_URL = "/api/v1.0/authors";
    private static final String ISSUES_URL = "/api/v1.0/issues";
    private static final String NOTICES_URL = "/api/v1.0/notices";

    @Test
    void createNoticeTest_shouldReturnCreated() {
        // Подготовка: автор → issue
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("noticeuser", "pass12345", "Notice", "User"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Test Issue", "Content", null),
                IssueResponseToDto.class);

        // Создаём notice
        var noticeRequest = new NoticeRequestToDto(issue.getBody().id(), "This is a notice");

        var response = restTemplate.postForEntity(NOTICES_URL, noticeRequest, NoticeResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getNoticeByIdTest_shouldReturnOk() {
        // Подготовка
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("usera", "12345678", "Abob", "Bboba"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Issue for notice", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issue.getBody().id(), "Notice content"),
                NoticeResponseToDto.class);
        assertThat(notice.getBody())
                .isNotNull();
        Long noticeId = notice.getBody().id();

        var response = restTemplate.getForEntity(NOTICES_URL + "/" + noticeId, NoticeResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getAllNoticesTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(NOTICES_URL, NoticeResponseToDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateNoticeTest_shouldReturnOk() {
        // Подготовка
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("updater", "pass12345", "Updater", "Nbb"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Update issue", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issue.getBody().id(), "Old notice"),
                NoticeResponseToDto.class);
        assertThat(notice.getBody())
                .isNotNull();

        Long noticeId = notice.getBody().id();
        var updateRequest = new NoticeRequestToDto(issue.getBody().id(), "Updated notice content");
        var putEntity = new HttpEntity<>(updateRequest);

        var response = restTemplate.exchange(
                NOTICES_URL + "/" + noticeId,
                HttpMethod.PUT,
                putEntity,
                NoticeResponseToDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deleteNoticeTest_shouldReturnNoContent() {
        // Подготовка
        var author = restTemplate.postForEntity(AUTHORS_URL,
                new AuthorRequestToDto("deleter", "12345678", "Del", "Nii"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Del issue", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issue.getBody().id(), "To delete"),
                NoticeResponseToDto.class);
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
                new AuthorRequestToDto("finder", "pass12345", "Finder", "Nss"),
                AuthorResponseToDto.class);
        assertThat(author.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(ISSUES_URL,
                new IssueRequestToDto(author.getBody().id(), "Target issue", "Content", null),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();
        restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issueId, "Notice 1"),
                NoticeResponseToDto.class);
        restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issueId, "Notice 2"),
                NoticeResponseToDto.class);

        var response = restTemplate.getForEntity(NOTICES_URL + "/issue/" + issueId, NoticeResponseToDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }
}
