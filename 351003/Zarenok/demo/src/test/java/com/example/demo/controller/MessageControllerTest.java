package com.example.demo.controller;

import com.example.demo.controller.BaseIntegrationTest;
import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MessageControllerTest extends BaseIntegrationTest {

    private final String BASE_URL = "/messages";

    private String uniqueLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String uniqueTitle() {
        return "title_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String uniqueContent() {
        return "content_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void createMessage_ValidData_ShouldReturn201() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Msg", "Author");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");

        MessageRequestTo request = new MessageRequestTo();
        request.setIssueId(issueId);
        request.setContent(uniqueContent());

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("content", equalTo(request.getContent()))
                .body("issueId", equalTo(issueId.intValue()));
    }

    @Test
    void createMessage_InvalidData_ShouldReturn400() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Invalid", "Msg");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");

        MessageRequestTo request = new MessageRequestTo();
        request.setIssueId(issueId);
        request.setContent("a"); // слишком короткий

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(400);
    }

    @Test
    void getMessageById_ExistingId_ShouldReturn200() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Get", "Msg");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");
        Long messageId = createTestMessage(issueId, uniqueContent());

        given()
                .when()
                .get(BASE_URL + "/{id}", messageId)
                .then()
                .statusCode(200)
                .body("id", equalTo(messageId.intValue()))
                .body("content", notNullValue())
                .body("issueId", equalTo(issueId.intValue()));
    }

    @Test
    void getMessageById_NotFound_ShouldReturn404() {
        given()
                .when()
                .get(BASE_URL + "/{id}", 9999L)
                .then()
                .statusCode(404);
    }

    @Test
    void getAllMessages_ShouldReturn200() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "List", "Msg");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");
        createTestMessage(issueId, uniqueContent());
        createTestMessage(issueId, uniqueContent());

        given()
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    void updateMessage_ValidData_ShouldReturn200() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Update", "Msg");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");
        Long messageId = createTestMessage(issueId, uniqueContent());

        MessageRequestTo updateReq = new MessageRequestTo();
        updateReq.setIssueId(issueId);
        updateReq.setContent(uniqueContent());

        given()
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put(BASE_URL + "/{id}", messageId)
                .then()
                .statusCode(200)
                .body("id", equalTo(messageId.intValue()))
                .body("content", equalTo(updateReq.getContent()))
                .body("issueId", equalTo(issueId.intValue()));
    }

    @Test
    void deleteMessage_ShouldReturn204() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Delete", "Msg");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");
        Long messageId = createTestMessage(issueId, uniqueContent());

        given()
                .when()
                .delete(BASE_URL + "/{id}", messageId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get(BASE_URL + "/{id}", messageId)
                .then()
                .statusCode(404);
    }

    // Хелперы
    private Long createTestAuthor(String login, String password, String firstname, String lastname) {
        AuthorRequestTo req = new AuthorRequestTo();
        req.setLogin(login);
        req.setPassword(password);
        req.setFirstname(firstname);
        req.setLastname(lastname);
        return given()
                .contentType(ContentType.JSON)
                .body(req)
                .post("/authors")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private Long createTestIssue(Long authorId, String title, String content) {
        IssueRequestTo req = new IssueRequestTo();
        req.setAuthorId(authorId);
        req.setTitle(title);
        req.setContent(content);
        req.setMarks(List.of());
        return given()
                .contentType(ContentType.JSON)
                .body(req)
                .post("/issues")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private Long createTestMessage(Long issueId, String content) {
        MessageRequestTo req = new MessageRequestTo();
        req.setIssueId(issueId);
        req.setContent(content);
        return given()
                .contentType(ContentType.JSON)
                .body(req)
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }
}