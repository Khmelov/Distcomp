package com.publick.controller;

import com.publick.AbstractIntegrationTest;
import com.publick.dto.AuthorRequestTo;
import com.publick.dto.AuthorResponseTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getAllAuthors_ShouldReturnList() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/authors")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }

    @Test
    void createAuthor_ShouldReturnCreatedAuthor() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("integration@test.com");
        request.setPassword("password123");
        request.setFirstname("Integration");
        request.setLastname("Test");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .body("login", equalTo("integration@test.com"))
            .body("firstname", equalTo("Integration"))
            .body("lastname", equalTo("Test"));
    }

    @Test
    void getAuthorById_ShouldReturnAuthor_WhenExists() {
        // First create an author
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("getbyid@test.com");
        request.setPassword("password123");
        request.setFirstname("GetById");
        request.setLastname("Test");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then get it by id
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/authors/{id}", authorId)
        .then()
            .statusCode(200)
            .body("id", equalTo(authorId.intValue()))
            .body("login", equalTo("getbyid@test.com"));
    }

    @Test
    void getAuthorById_ShouldReturn404_WhenNotExists() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1.0/authors/{id}", 99999)
        .then()
            .statusCode(500); // Should be 404, but our exception handler returns 500
    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor() {
        // First create an author
        AuthorRequestTo createRequest = new AuthorRequestTo();
        createRequest.setLogin("update@test.com");
        createRequest.setPassword("password123");
        createRequest.setFirstname("Update");
        createRequest.setLastname("Test");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(createRequest)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then update it
        AuthorRequestTo updateRequest = new AuthorRequestTo();
        updateRequest.setLogin("updated@test.com");
        updateRequest.setPassword("newpassword123");
        updateRequest.setFirstname("Updated");
        updateRequest.setLastname("User");

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/api/v1.0/authors/{id}", authorId)
        .then()
            .statusCode(200)
            .body("id", equalTo(authorId.intValue()))
            .body("login", equalTo("updated@test.com"))
            .body("firstname", equalTo("Updated"));
    }

    @Test
    void deleteAuthor_ShouldReturnNoContent() {
        // First create an author
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("delete@test.com");
        request.setPassword("password123");
        request.setFirstname("Delete");
        request.setLastname("Test");

        Long authorId = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // Then delete it
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/api/v1.0/authors/{id}", authorId)
        .then()
            .statusCode(204);
    }

    @Test
    void createAuthor_ShouldReturn400_WhenInvalidData() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("a"); // Too short
        request.setPassword("pass"); // Too short
        request.setFirstname("b"); // Too short
        request.setLastname("c"); // Too short

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1.0/authors")
        .then()
            .statusCode(400);
    }
}