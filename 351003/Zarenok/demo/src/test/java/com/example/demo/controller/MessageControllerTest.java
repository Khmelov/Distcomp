package com.example.demo.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void createMessage_shouldReturn201() {
        Long authorId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                    {
                                      "login": "m12@mail.com",
                                      "password": "password123",
                                      "firstname": "Msg",
                                      "lastname": "Owner"
                                    }
                                """)
                        .when()
                        .post("/api/v1.0/authors")
                        .then()
                        .extract()
                        .path("id");

        Long issueId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                    {
                                      "authorId": %d,
                                      "title": "Message Issue",
                                      "content": "Issue Content"
                                    }
                                """.formatted(authorId))
                        .when()
                        .post("/api/v1.0/issues")
                        .then()
                        .extract()
                        .path("id");

        given()
                .contentType(ContentType.JSON)
                .body("""
                            {
                              "issueId": %d,
                              "content": "Test Message"
                            }
                        """.formatted(issueId))
                .when()
                .post("/api/v1.0/messages")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("content", equalTo("Test Message"));
    }

    @Test
    void getMessage_whenNotExists_shouldReturn404() {
        given()
                .when()
                .get("/api/v1.0/messages/999999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(40401));
    }

    @Test
    void createMessage_withInvalidContent_shouldReturn400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                            {
                              "issueId": 1,
                              "content": ""
                            }
                        """)
                .when()
                .post("/api/v1.0/messages")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(40001));
    }

    @Test
    void getMessages_withPagination_shouldReturnPage() {
        given()
                .when()
                .get("/api/v1.0/messages?page=0&size=5")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }
}
