package org.discussion.integration.service;

import org.discussion.integration.configuration.CassandraIntegrationTest;
import org.discussion.dto.request.NoticeRequestToDto;
import org.discussion.dto.response.NoticeResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeControllerIntegrationTest extends CassandraIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String NOTICES_URL = "/api/v1.0/notices";

    @Test
    void createNoticeTest_shouldReturnCreated() {
        var issueId = 15L;
        var noticeRequest = new NoticeRequestToDto(issueId, "This is a notice");

        var response = restTemplate.postForEntity(NOTICES_URL, noticeRequest, NoticeResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getNoticeByIdTest_shouldReturnOk() {
        // Подготовка
        var issueId = 15L;
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issueId, "Notice content"),
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
        var issueId = 15L;
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issueId, "Old notice"),
                NoticeResponseToDto.class);
        assertThat(notice.getBody())
                .isNotNull();

        Long noticeId = notice.getBody().id();
        var updateRequest = new NoticeRequestToDto(issueId, "Updated notice content");
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
        var issueId = 15L;
        var notice = restTemplate.postForEntity(NOTICES_URL,
                new NoticeRequestToDto(issueId, "To delete"),
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
        Long issueId = 15L;
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
