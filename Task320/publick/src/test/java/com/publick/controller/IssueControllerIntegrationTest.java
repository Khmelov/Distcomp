package com.publick.controller;

import com.publick.AbstractIntegrationTest;
import com.publick.dto.AuthorRequestTo;
import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IssueControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getAllIssues_ShouldReturnList() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/issues")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void createIssue_ShouldReturnCreatedIssue() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("issueauthor@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Issue");
        authorRequest.setLastname("Author");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(authorRequest)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create an issue
        IssueRequestTo request = new IssueRequestTo();
        request.setAuthorId(authorId);
        request.setTitle("Test Issue");
        request.setContent("This is a test issue content");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .body("title", equalTo("Test Issue"))
            .body("content", equalTo("This is a test issue content"))
            .body("authorId", equalTo(authorId.intValue()));
    }

    @Test
    void getIssueById_ShouldReturnIssue_WhenExists() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("getissue@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Get");
        authorRequest.setLastname("Issue");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(authorRequest)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create an issue
        IssueRequestTo issueRequest = new IssueRequestTo();
        issueRequest.setAuthorId(authorId);
        issueRequest.setTitle("Get Issue Test");
        issueRequest.setContent("Test content for get issue");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(issueRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then get it by id
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/issues/{id}", issueId)
        .then()
            .statusCode(200)
            .body("id", equalTo(issueId.intValue()))
            .body("title", equalTo("Get Issue Test"));
    }

    @Test
    void updateIssue_ShouldReturnUpdatedIssue() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("updateissue@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Update");
        authorRequest.setLastname("Issue");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(authorRequest)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Create another author for update
        AuthorRequestTo authorRequest2 = new AuthorRequestTo();
        authorRequest2.setLogin("updateissue2@test.com");
        authorRequest2.setPassword("password123");
        authorRequest2.setFirstname("Update2");
        authorRequest2.setLastname("Issue2");

        Long authorId2 = given()
            .contentType(ContentType.JSON)
            .body(authorRequest2)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create an issue
        IssueRequestTo createRequest = new IssueRequestTo();
        createRequest.setAuthorId(authorId);
        createRequest.setTitle("Update Issue Test");
        createRequest.setContent("Original content");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(createRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then update it
        IssueRequestTo updateRequest = new IssueRequestTo();
        updateRequest.setAuthorId(authorId2);
        updateRequest.setTitle("Updated Issue Test");
        updateRequest.setContent("Updated content");

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/api/v1.0/issues/{id}", issueId)
        .then()
            .statusCode(200)
            .body("id", equalTo(issueId.intValue()))
            .body("title", equalTo("Updated Issue Test"))
            .body("content", equalTo("Updated content"))
            .body("authorId", equalTo(authorId2.intValue()));
    }

    @Test
    void deleteIssue_ShouldReturnNoContent() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("deleteissue@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Delete");
        authorRequest.setLastname("Issue");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(authorRequest)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create an issue
        IssueRequestTo issueRequest = new IssueRequestTo();
        issueRequest.setAuthorId(authorId);
        issueRequest.setTitle("Delete Issue Test");
        issueRequest.setContent("Test content for delete issue");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(issueRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then delete it
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/api/v1.0/issues/{id}", issueId)
        .then()
            .statusCode(204);
    }

    @Test
    void createIssue_ShouldReturn400_WhenInvalidData() {
        IssueRequestTo request = new IssueRequestTo();
        request.setAuthorId(1L);
        request.setTitle("A"); // Too short
        request.setContent("Short"); // Too short

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(400);
    }
}