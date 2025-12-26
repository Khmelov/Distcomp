package com.restApp.controller;

import com.restApp.dto.AuthorRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testCreateAndGetAuthor() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("author_test_" + System.currentTimeMillis());
        request.setPassword("secret");
        request.setFirstname("John");
        request.setLastname("Doe");

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(201)
                .body("login", equalTo(request.getLogin()))
                .extract().path("id");

        given()
                .when()
                .get("/api/v1.0/authors/" + id)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"));
    }
}
