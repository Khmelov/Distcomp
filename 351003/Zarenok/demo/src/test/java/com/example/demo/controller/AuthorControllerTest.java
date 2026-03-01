package com.example.demo.controller;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
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
@Testcontainers
public class AuthorControllerTest {
    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    @Test
    void createAuthor_shouldReturn201AndResponseTo() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "login": "new@mail.com",
                      "password": "password123",
                      "firstname": "New",
                      "lastname": "User"
                    }
                """)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("login", equalTo("new@mail.com"));
    }

    @Test
    void getAuthor_whenNotExists_shouldReturn404() {
        given()
                .when()
                .get("/api/v1.0/authors/999999")
                .then()
                .statusCode(404)
                .body("errorMessage", containsString("not found"))
                .body("errorCode", equalTo(40401));
    }

    @Test
    void createAuthor_withInvalidData_shouldReturn400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "login": "a",
                  "password": "123",
                  "firstname": "",
                  "lastname": ""
                }""").when().post("/api/v1.0/authors").then()
                .statusCode(400).body("errorCode", equalTo(40001));
    }
}
