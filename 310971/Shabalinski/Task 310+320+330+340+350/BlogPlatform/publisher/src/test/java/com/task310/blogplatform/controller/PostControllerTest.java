package com.task310.blogplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task310.blogplatform.dto.PostResponseTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

    private static MockWebServer mockWebServer;
    private static String mockServerUrl;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.xml");
        
        // Start MockWebServer for discussion service
        try {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
            mockServerUrl = "http://localhost:" + mockWebServer.getPort();
            registry.add("discussion.service.url", () -> mockServerUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start MockWebServer", e);
        }
    }

    @LocalServerPort
    private int port;

    private Long testUserId;
    private Long testArticleId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws IOException {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        // Create User and Article for tests
        testUserId = createUser();
        testArticleId = createArticle(testUserId);
        
        // Clear previous mock responses - just clear the queue, don't recreate server
        if (mockWebServer != null) {
            // Server is already running from @DynamicPropertySource
            // Just ensure it's ready for new requests
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
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
    
    @org.junit.jupiter.api.AfterAll
    static void tearDownAll() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
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

    private PostResponseTo createMockPostResponse(Long id, Long articleId, String content) {
        PostResponseTo post = new PostResponseTo();
        post.setId(id);
        post.setArticleId(articleId);
        post.setContent(content);
        post.setCreated(LocalDateTime.now());
        post.setModified(LocalDateTime.now());
        return post;
    }

    @Test
    public void testCreatePost() throws Exception {
        Long postId = 1L;
        PostResponseTo mockResponse = createMockPostResponse(postId, testArticleId, "This is a test post content");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));

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
    public void testGetAllPosts() throws Exception {
        PostResponseTo mockPost = createMockPostResponse(1L, testArticleId, "Post Content");
        List<PostResponseTo> mockPosts = List.of(mockPost);
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(mockPosts))
                .addHeader("Content-Type", "application/json"));

        given()
                .when()
                .get("/api/v1.0/posts")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    public void testGetPostById() throws Exception {
        Long postId = 1L;
        PostResponseTo mockResponse = createMockPostResponse(postId, testArticleId, "Post Content 2");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));

        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Post Content 2"
                }
                """, testArticleId);

        Long createdPostId = given()
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
                .get("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdPostId.intValue()))
                .body("content", equalTo("Post Content 2"));
    }

    @Test
    public void testUpdatePost() throws Exception {
        Long postId = 1L;
        PostResponseTo mockResponse = createMockPostResponse(postId, testArticleId, "Original Content");
        PostResponseTo updatedResponse = createMockPostResponse(postId, testArticleId, "Updated Content");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(updatedResponse))
                .addHeader("Content-Type", "application/json"));

        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Original Content"
                }
                """, testArticleId);

        Long createdPostId = given()
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
                .put("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(200)
                .body("content", equalTo("Updated Content"));
    }

    @Test
    public void testDeletePost() throws Exception {
        Long postId = 1L;
        PostResponseTo mockResponse = createMockPostResponse(postId, testArticleId, "To Delete");
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "To Delete"
                }
                """, testArticleId);

        Long createdPostId = given()
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
                .delete("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testConnectPosts() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[]")
                .addHeader("Content-Type", "application/json"));
        
        // Check GET Status Code
        given()
                .when()
                .get("/api/v1.0/posts")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetPostsByArticleId() throws Exception {
        PostResponseTo mockPost = createMockPostResponse(1L, testArticleId, "Post 1");
        List<PostResponseTo> mockPosts = List.of(mockPost);
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(objectMapper.writeValueAsString(mockPost))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(mockPosts))
                .addHeader("Content-Type", "application/json"));

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
        // Invalid articleId - should fail in publisher before calling discussion
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
    public void testRegressionCheckRestPost() throws Exception {
        Long postId = 1L;
        PostResponseTo mockResponse = createMockPostResponse(postId, testArticleId, "Regression Post Content");
        PostResponseTo updatedResponse = createMockPostResponse(postId, testArticleId, "Updated Regression Content");
        List<PostResponseTo> mockPosts = List.of(mockResponse);
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(mockPosts))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(updatedResponse))
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));
        
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        // CREATE
        String postJson = String.format("""
                {
                    "articleId": %d,
                    "content": "Regression Post Content"
                }
                """, testArticleId);

        Long createdPostId = given()
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
                .get("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdPostId.intValue()))
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
                .put("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(200)
                .body("content", equalTo("Updated Regression Content"));

        // DELETE
        given()
                .when()
                .delete("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/posts/" + createdPostId)
                .then()
                .statusCode(404);
    }
}
