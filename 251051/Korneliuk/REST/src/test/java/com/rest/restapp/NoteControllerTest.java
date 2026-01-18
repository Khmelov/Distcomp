package com.rest.restapp;

import com.rest.restapp.dto.request.UserRequestToDto;
import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.request.NoteRequestToDto;
import com.rest.restapp.dto.response.UserResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import com.rest.restapp.dto.response.NoteResponseTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String USERS_URL = "/api/v1.0/users";
    private static final String ISSUES_URL = "/api/v1.0/issues";
    private static final String NOTICES_URL = "/api/v1.0/notes";

    @Test
    void createNoticeTest_shouldReturnCreated() {
        // Подготовка: автор → issue
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "noticeuser",
                        "pass",
                        "Notice",
                        "User"
                ),
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Test Issue",
                        "Content"
                ),
                IssueResponseToDto.class
        );
        var noticeRequest = new NoteRequestToDto(
                Objects.requireNonNull(issue.getBody()).id(),
                "This is a notice"
        );
        var response = restTemplate.postForEntity(
                NOTICES_URL,
                noticeRequest,
                NoteResponseTo.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getNoticeByIdTest_shouldReturnOk() {
        // Подготовка
        var user = restTemplate.postForEntity(USERS_URL,
                new UserRequestToDto(
                        "usera",
                        "123",
                        "A",
                        "B"
                ),
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Issue for notice",
                        "Content"
                ),
                IssueResponseToDto.class
        );
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(
                NOTICES_URL,
                new NoteRequestToDto(
                        issue.getBody().id(),
                        "Notice content"
                ),
                NoteResponseTo.class
        );
        assertThat(notice.getBody())
                .isNotNull();
        Long noticeId = notice.getBody().id();

        var response = restTemplate.getForEntity(
                NOTICES_URL + "/" + noticeId,
                NoteResponseTo.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getAllNoticesTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(
                NOTICES_URL,
                NoteResponseTo[].class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void updateNoticeTest_shouldReturnOk() {
        // Подготовка
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "updater",
                        "pass",
                        "Updater",
                        "N"
                ),
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Update issue",
                        "Content"),
                IssueResponseToDto.class
        );
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(
                NOTICES_URL,
                new NoteRequestToDto(
                        issue.getBody().id(),
                        "Old notice"
                ),
                NoteResponseTo.class
        );
        assertThat(notice.getBody())
                .isNotNull();

        Long noticeId = notice.getBody().id();
        var updateRequest = new NoteRequestToDto(
                issue.getBody().id(),
                "Updated notice content"
        );

        var response = restTemplate.exchange(
                NOTICES_URL + "/" + noticeId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                NoteResponseTo.class
        );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void deleteNoticeTest_shouldReturnNoContent() {
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "deleter",
                        "123",
                        "Del",
                        "N"
                ),
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Del issue",
                        "Content"
                ),
                IssueResponseToDto.class
        );
        assertThat(issue.getBody())
                .isNotNull();
        var notice = restTemplate.postForEntity(
                NOTICES_URL,
                new NoteRequestToDto(
                        issue.getBody().id(),
                        "To delete"
                ),
                NoteResponseTo.class
        );
        assertThat(notice.getBody())
                .isNotNull();

        var response = restTemplate.exchange(
                NOTICES_URL + "/" + notice.getBody().id(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getNoticesByIssueIdTest_shouldReturnOk() {
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "finder",
                        "pass",
                        "Finder",
                        "N"),
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();

        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Target issue",
                        "Content"
                ),
                IssueResponseToDto.class
        );
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();
        restTemplate.postForEntity(
                NOTICES_URL,
                new NoteRequestToDto(issueId,"Notice 1"),
                NoteResponseTo.class
        );
        restTemplate.postForEntity(
                NOTICES_URL,
                new NoteRequestToDto(issueId, "Notice 2"),
                NoteResponseTo.class
        );

        var response = restTemplate.getForEntity(
                NOTICES_URL + "/issue/" + issueId,
                NoteResponseTo[].class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
        assertThat(response.getBody())
                .hasSizeGreaterThanOrEqualTo(2);
    }
}
