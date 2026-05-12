package com.example.demo.controller;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.IssueRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class IssueControllerTest extends BaseIntegrationTest {

    private final String BASE_URL = "/issues";

    private String uniqueLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String uniqueTitle() {
        return "title_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void createIssue_ValidData_ShouldReturn201() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Issue", "Author");

        IssueRequestTo request = new IssueRequestTo();
        request.setAuthorId(authorId);
        request.setTitle(uniqueTitle());
        request.setContent("Test content");
        request.setMarks(List.of("mark1", "mark2")); // если метки создаются автоматически

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(request.getTitle()))
                .body("authorId", equalTo(authorId.intValue()));
    }

    @Test
    void createIssue_InvalidData_ShouldReturn400() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Invalid", "Issue");

        IssueRequestTo request = new IssueRequestTo();
        request.setAuthorId(authorId);
        request.setTitle("a"); // слишком короткий
        request.setContent("Content");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(400);
    }

    @Test
    void getIssueById_ExistingId_ShouldReturn200() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Get", "Issue");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");

        given()
                .when()
                .get(BASE_URL + "/{id}", issueId)
                .then()
                .statusCode(200)
                .body("id", equalTo(issueId.intValue()))
                .body("title", notNullValue());
    }

    @Test
    void getIssueById_NotFound_ShouldReturn404() {
        given()
                .when()
                .get(BASE_URL + "/{id}", 9999L)
                .then()
                .statusCode(404);
    }

    @Test
    void getAllIssues_ShouldReturn200() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "List", "Author");
        createTestIssue(authorId, uniqueTitle(), "Content");
        createTestIssue(authorId, uniqueTitle(), "Content");

        given()
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    void updateIssue_ValidData_ShouldReturn200() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Update", "Author");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Old Content");

        IssueRequestTo updateReq = new IssueRequestTo();
        updateReq.setAuthorId(authorId);
        updateReq.setTitle(uniqueTitle());
        updateReq.setContent("New Content");
        updateReq.setMarks(List.of());

        given()
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put(BASE_URL + "/{id}", issueId)
                .then()
                .statusCode(200)
                .body("id", equalTo(issueId.intValue()))
                .body("title", equalTo(updateReq.getTitle()));
    }

    @Test
    void deleteIssue_ShouldReturn204() {
        Long authorId = createTestAuthor(uniqueLogin(), "password", "Delete", "Issue");
        Long issueId = createTestIssue(authorId, uniqueTitle(), "Content");

        given()
                .when()
                .delete(BASE_URL + "/{id}", issueId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get(BASE_URL + "/{id}", issueId)
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
        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .body(req)
                .post("/authors");
        if (response.getStatusCode() != 201) {
            System.out.println("=== Ошибка создания автора ===");
            System.out.println("Статус: " + response.getStatusCode());
            System.out.println("Тело: " + response.getBody().asString());
        }
        response.then().statusCode(201);
        return response.jsonPath().getLong("id");
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
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }
}