package com.rest.restapp;

import com.rest.restapp.dto.request.UserRequestToDto;
import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.UserResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IssueControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ISSUES_URL = "/api/v1.0/issues";
    private static final String USERS_URL = "/api/v1.0/users";

    @Test
    void createIssueTest_shouldReturnCreated() {
        var userRequest = new UserRequestToDto(
                "issueuser",
                "pass",
                "Issue",
                "User"
        );
        var user = restTemplate.postForEntity(
                USERS_URL,
                userRequest,
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();
        Long userId = user.getBody().id();

        var issueRequest = new IssueRequestToDto(
                userId,
                "Bug report",
                "App crashes on startup"
        );
        var response = restTemplate.postForEntity(
                ISSUES_URL,
                issueRequest,
                IssueResponseToDto.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getIssueByIdTest_shouldReturnOk() {
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "user2",
                        "123",
                        "Test",
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
                        "Title",
                        "Content"
                ),
                IssueResponseToDto.class
        );
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();

        var response = restTemplate.getForEntity(
                ISSUES_URL + "/" + issueId,
                IssueResponseToDto.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getAllIssuesTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(
                ISSUES_URL,
                IssueResponseToDto[].class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void updateIssueTest_shouldReturnOk() {
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "updater",
                        "pass",
                        "Updater",
                        "Man"
                ),
                UserResponseToDto.class
        );
        assertThat(user.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Old Title",
                        "Old Content"
                ),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();
        var updatedRequest = new IssueRequestToDto(
                user.getBody().id(),
                "New Title",
                "New Content"
        );
        var response = restTemplate.exchange(
                ISSUES_URL + "/" + issueId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedRequest),
                IssueResponseToDto.class
        );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void deleteIssueTest_shouldReturnNoContent() {
        var user = restTemplate.postForEntity(
                USERS_URL,
                new UserRequestToDto(
                        "deleter",
                        "123",
                        "Del",
                        "User"
                ),
                UserResponseToDto.class);
        assertThat(user.getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "To Delete",
                        "Content"
                ),
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
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getUserByIssueIdTest_shouldReturnOk() {
        var userRequest = new UserRequestToDto(
                "linkeduser",
                "pass",
                "Linked",
                "User"
        );
        var user = restTemplate.postForEntity(
                USERS_URL,
                userRequest,
                UserResponseToDto.class
        );
        assertThat(user
                .getBody())
                .isNotNull();
        var issue = restTemplate.postForEntity(
                ISSUES_URL,
                new IssueRequestToDto(
                        user.getBody().id(),
                        "Linked Issue",
                        "Content"
                ),
                IssueResponseToDto.class);
        assertThat(issue.getBody())
                .isNotNull();

        Long issueId = issue.getBody().id();

        var response = restTemplate.getForEntity(ISSUES_URL + "/" + issueId + "/user", UserResponseToDto.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
        assertThat(response.getBody().id())
                .isEqualTo(user
                        .getBody()
                        .id());
    }
}