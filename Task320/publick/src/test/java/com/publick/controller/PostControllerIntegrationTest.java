package com.publick.controller;

import com.publick.AbstractIntegrationTest;
import com.publick.dto.AuthorRequestTo;
import com.publick.dto.IssueRequestTo;
import com.publick.dto.PostRequestTo;
import com.publick.dto.PostResponseTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getAllPosts_ShouldReturnList() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/posts")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void createPost_ShouldReturnCreatedPost() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("postauthor@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Post");
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
        IssueRequestTo issueRequest = new IssueRequestTo();
        issueRequest.setAuthorId(authorId);
        issueRequest.setTitle("Post Issue Test");
        issueRequest.setContent("Issue content for post test");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(issueRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create a post
        PostRequestTo request = new PostRequestTo();
        request.setIssueId(issueId);
        request.setContent("This is a test post content");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/posts")
        .then()
            .statusCode(201)
            .body("content", equalTo("This is a test post content"))
            .body("issueId", equalTo(issueId.intValue()));
    }

    @Test
    void getPostById_ShouldReturnPost_WhenExists() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("getpost@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Get");
        authorRequest.setLastname("Post");

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
        issueRequest.setTitle("Get Post Issue Test");
        issueRequest.setContent("Issue content for get post test");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(issueRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create a post
        PostRequestTo postRequest = new PostRequestTo();
        postRequest.setIssueId(issueId);
        postRequest.setContent("Test content for get post");

        Long postId = given()
            .contentType(ContentType.JSON)
            .body(postRequest)
        .when()
            .post("/api/v1.0/posts")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then get it by id
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/posts/{id}", postId)
        .then()
            .statusCode(200)
            .body("id", equalTo(postId.intValue()))
            .body("content", equalTo("Test content for get post"));
    }

    @Test
    void updatePost_ShouldReturnUpdatedPost() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("updatepost@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Update");
        authorRequest.setLastname("Post");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(authorRequest)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Create an issue
        IssueRequestTo issueRequest = new IssueRequestTo();
        issueRequest.setAuthorId(authorId);
        issueRequest.setTitle("Update Post Issue Test");
        issueRequest.setContent("Issue content for update post test");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(issueRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Create another issue
        IssueRequestTo issueRequest2 = new IssueRequestTo();
        issueRequest2.setAuthorId(authorId);
        issueRequest2.setTitle("Update Post Issue Test 2");
        issueRequest2.setContent("Issue content for update post test 2");

        Long issueId2 = given()
            .contentType(ContentType.JSON)
            .body(issueRequest2)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create a post
        PostRequestTo createRequest = new PostRequestTo();
        createRequest.setIssueId(issueId);
        createRequest.setContent("Original post content");

        Long postId = given()
            .contentType(ContentType.JSON)
            .body(createRequest)
        .when()
            .post("/api/v1.0/posts")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then update it
        PostRequestTo updateRequest = new PostRequestTo();
        updateRequest.setIssueId(issueId2);
        updateRequest.setContent("Updated post content");

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/api/v1.0/posts/{id}", postId)
        .then()
            .statusCode(200)
            .body("id", equalTo(postId.intValue()))
            .body("content", equalTo("Updated post content"))
            .body("issueId", equalTo(issueId2.intValue()));
    }

    @Test
    void deletePost_ShouldReturnNoContent() {
        // First create an author
        AuthorRequestTo authorRequest = new AuthorRequestTo();
        authorRequest.setLogin("deletepost@test.com");
        authorRequest.setPassword("password123");
        authorRequest.setFirstname("Delete");
        authorRequest.setLastname("Post");

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
        issueRequest.setTitle("Delete Post Issue Test");
        issueRequest.setContent("Issue content for delete post test");

        Long issueId = given()
            .contentType(ContentType.JSON)
            .body(issueRequest)
        .when()
            .post("/api/v1.0/issues")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then create a post
        PostRequestTo postRequest = new PostRequestTo();
        postRequest.setIssueId(issueId);
        postRequest.setContent("Test content for delete post");

        Long postId = given()
            .contentType(ContentType.JSON)
            .body(postRequest)
        .when()
            .post("/api/v1.0/posts")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then delete it
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/api/v1.0/posts/{id}", postId)
        .then()
            .statusCode(204);
    }

    @Test
    void createPost_ShouldReturn400_WhenInvalidData() {
        PostRequestTo request = new PostRequestTo();
        request.setIssueId(1L);
        request.setContent("Short"); // Too short

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/posts")
        .then()
            .statusCode(400);
    }
}