package com.task310.discussion.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PostControllerTest {

    @Container
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1")
            .withInitScript("init-cassandra.cql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.contact-points", cassandra::getHost);
        registry.add("spring.data.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.data.cassandra.local-datacenter", () -> "datacenter1");
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testCreatePost() {
        String postJson = """
                {
                    "articleId": 1,
                    "content": "This is a test post content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(201)
                .body("content", equalTo("This is a test post content"))
                .body("id", notNullValue());
    }

    @Test
    public void testGetAllPosts() {
        String postJson = """
                {
                    "articleId": 1,
                    "content": "Post Content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts");

        given()
                .when()
                .get("/api/v1.0/posts")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    public void testGetPostsByArticleId() {
        String postJson = """
                {
                    "articleId": 1,
                    "content": "Post 1"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts");

        given()
                .when()
                .get("/api/v1.0/articles/1/posts")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }
}

