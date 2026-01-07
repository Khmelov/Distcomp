package com.task310.blogplatform.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class PostControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.xml");
    }

    @LocalServerPort
    private int port;

    private Long testUserId;
    private Long testArticleId;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        // Create User and Article for tests
        testUserId = createUser();
        testArticleId = createArticle(testUserId);
    }

    @AfterEach
    public void tearDown() {
        // Cleanup: Delete Article and User
        if (testArticleId != null) {
            given()
                    .when()
                    .delete("/api/v1.0/articles/" + testArticleId)
                    .then()
                    .statusCode(204);
        }
        if (testUserId != null) {
            given()
                    .when()
                    .delete("/api/v1.0/users/" + testUserId)
                    .then()
                    .statusCode(204);
        }
    }

    private Long createUser() {
        String userJson = """
                {
                    "login": "postuser",
                    "password": "password123",
                    "firstname": "Post",
                    "lastname": "User"
                }
                """;

        return given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private Long createArticle(Long userId) {
        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Test Article",
                    "content": "Article Content"
                }
                """, userId);

        return given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    public void testCreatePost() {
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "This is a test post content"
                }
                """, testArticleId);

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
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Post Content"
                }
                """, testArticleId);

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
    public void testGetPostById() {
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Post Content 2"
                }
                """, testArticleId);

        Long postId = given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(200)
                .body("id", equalTo(postId.intValue()))
                .body("content", equalTo("Post Content 2"));
    }

    @Test
    public void testUpdatePost() {
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Original Content"
                }
                """, testArticleId);

        Long postId = given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Updated Content"
                }
                """, testArticleId);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(200)
                .body("content", equalTo("Updated Content"));
    }

    @Test
    public void testDeletePost() {
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "To Delete"
                }
                """, testArticleId);

        Long postId = given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testConnectPosts() {
        // Check GET Status Code
        given()
                .when()
                .get("/api/v1.0/posts")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetPostsByArticleId() {
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Post 1"
                }
                """, testArticleId);

        given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts");

        given()
                .when()
                .get("/api/v1.0/articles/" + testArticleId + "/posts")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    public void testCreatePostWithInvalidFields() {
        // Empty content
        String invalidJson = String.format("""
                {
                    "articleId": %d,
                    "content": ""
                }
                """, testArticleId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Missing content
        invalidJson = String.format("""
                {
                    "articleId": %d
                }
                """, testArticleId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Id in body
        invalidJson = String.format("""
                {
                    "id": 1,
                    "articleId": %d,
                    "content": "Content"
                }
                """, testArticleId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));
    }

    @Test
    public void testCreatePostWithInvalidAssociation() {
        // Invalid articleId
        String invalidJson = """
                {
                    "articleId": 99999,
                    "content": "Content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));
    }

    @Test
    public void testRegressionCheckRestPost() {
        // CREATE
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Regression Post Content"
                }
                """, testArticleId);

        Long postId = given()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(201)
                .body("content", equalTo("Regression Post Content"))
                .body("id", notNullValue())
                .extract()
                .path("id");

        // READ by ID
        given()
                .when()
                .get("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(200)
                .body("id", equalTo(postId.intValue()))
                .body("content", equalTo("Regression Post Content"));

        // READ ALL
        given()
                .when()
                .get("/api/v1.0/posts")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));

        // UPDATE
        String updateJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Updated Regression Content"
                }
                """, testArticleId);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(200)
                .body("content", equalTo("Updated Regression Content"));

        // DELETE
        given()
                .when()
                .delete("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/posts/" + postId)
                .then()
                .statusCode(404);
    }
}

