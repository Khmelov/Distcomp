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
public class IssueControllerTest {
    @LocalServerPort
    private int port;

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void createIssue_shouldReturn201() {
        Long authorId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                    {
                      "login": "issue@mail.com",
                      "password": "password123",
                      "firstname": "Issue",
                      "lastname": "Owner"
                    }
                """)
                        .when()
                        .post("/api/v1.0/authors")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        given()
                .contentType(ContentType.JSON)
                .body("""
            {
              "authorId": %d,
              "title": "Test Issue",
              "content": "Test content for issue"
            }
        """.formatted(authorId))
                .when()
                .post("/api/v1.0/issues")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Test Issue"));
    }

    @Test
    void getIssueById_shouldReturn200() {

        Long authorId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                    {
                      "login": "get@mail.com",
                      "password": "password123",
                      "firstname": "Get",
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
                      "title": "Issue Title",
                      "content": "Issue Content"
                    }
                """.formatted(authorId))
                        .when()
                        .post("/api/v1.0/issues")
                        .then()
                        .extract()
                        .path("id");

        given()
                .when()
                .get("/api/v1.0/issues/" + issueId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Issue Title"));
    }

    @Test
    void getIssue_whenNotExists_shouldReturn404() {
        given()
                .when()
                .get("/api/v1.0/issues/999999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(40401));
    }

    @Test
    void getIssues_withPagination_shouldReturnPage() {

        given()
                .when()
                .get("/api/v1.0/issues?page=0&size=5&sortBy=id&sortDir=asc")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    void createIssue_withInvalidTitle_shouldReturn400() {

        given()
                .contentType(ContentType.JSON)
                .body("""
            {
              "authorId": 1,
              "title": "a",
              "content": ""
            }
        """)
                .when()
                .post("/api/v1.0/issues")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(40001));
    }
}
