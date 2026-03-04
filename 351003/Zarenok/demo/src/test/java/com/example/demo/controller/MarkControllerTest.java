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
class MarkControllerTest {

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
    void createMark_shouldReturn201() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                            {
                              "name": "Important"
                            }
                        """)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Important"));
    }

    @Test
    void getMarkById_shouldReturn200() {

        Long id =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                                    {
                                      "name": "Bug"
                                    }
                                """)
                        .when()
                        .post("/api/v1.0/marks")
                        .then()
                        .extract()
                        .path("id");

        given()
                .when()
                .get("/api/v1.0/marks/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Bug"));
    }

    @Test
    void getMark_whenNotExists_shouldReturn404() {
        given()
                .when()
                .get("/api/v1.0/marks/99999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(40401));
    }

    @Test
    void createMark_withInvalidName_shouldReturn400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                            {
                              "name": "a"
                            }
                        """)
                .when()
                .post("/api/v1.0/marks")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo(40001));
    }

    @Test
    void getMarks_withPagination_shouldReturnPage() {
        given()
                .when()
                .get("/api/v1.0/marks?page=0&size=5&sortBy=id&sortDir=asc")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }
}
