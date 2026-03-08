package com.example.demo.controller;

import com.example.demo.controller.BaseIntegrationTest;
import com.example.demo.dto.requests.AuthorRequestTo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthorControllerTest extends BaseIntegrationTest {

    private final String BASE_URL = "/authors";

    private String uniqueLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void createAuthor_ValidData_ShouldReturn201() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin(uniqueLogin());
        request.setPassword("password123");
        request.setFirstname("John");
        request.setLastname("Doe");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("login", equalTo(request.getLogin()))
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"));
    }

    @Test
    void createAuthor_InvalidData_ShouldReturn400() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("a");
        request.setPassword("short");
        request.setFirstname("John");
        request.setLastname("Doe");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URL)
                .then()
                .statusCode(400);
    }

    @Test
    void getAuthorById_ExistingId_ShouldReturn200() {
        Long id = createTestAuthor(uniqueLogin(), "password123", "Get", "Test");

        given()
                .when()
                .get(BASE_URL + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("login", notNullValue());
    }

    @Test
    void getAuthorById_NotFound_ShouldReturn404() {
        given()
                .when()
                .get(BASE_URL + "/{id}", 9999L)
                .then()
                .statusCode(404);
    }

    @Test
    void getAllAuthors_ShouldReturn200() {
        createTestAuthor(uniqueLogin(), "password123", "User", "One");
        createTestAuthor(uniqueLogin(), "password123", "User", "Two");

        given()
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    void updateAuthor_ValidData_ShouldReturn200() {
        Long id = createTestAuthor(uniqueLogin(), "password123", "Old", "Name");

        AuthorRequestTo updateReq = new AuthorRequestTo();
        updateReq.setLogin(uniqueLogin());
        updateReq.setPassword("newpassword123");
        updateReq.setFirstname("New");
        updateReq.setLastname("Name");

        given()
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put(BASE_URL + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("login", equalTo(updateReq.getLogin()));
    }

    @Test
    void deleteAuthor_ShouldReturn204() {
        Long id = createTestAuthor(uniqueLogin(), "password123", "Delete", "Me");

        given()
                .when()
                .delete(BASE_URL + "/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get(BASE_URL + "/{id}", id)
                .then()
                .statusCode(404);
    }

    private Long createTestAuthor(String login, String password, String firstname, String lastname) {
        AuthorRequestTo req = new AuthorRequestTo();
        req.setLogin(login);
        req.setPassword(password);
        req.setFirstname(firstname);
        req.setLastname(lastname);
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