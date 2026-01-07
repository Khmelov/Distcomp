package com.task310.blogplatform.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
public class ArticleControllerTest {

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

    private static Long testUserId;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @BeforeAll
    public static void beforeAll() {
        // This will be called before all tests
        // User creation will be done in individual tests or in setUp if needed
    }

    @AfterAll
    public static void afterAll() {
        // Clean up test user if it was created
        if (testUserId != null) {
            RestAssured.baseURI = "http://localhost";
            // Note: port might not be available in @AfterAll, so cleanup might need to be done differently
        }
    }

    private Long createUser() {
        String uniqueLogin = "articleuser" + System.currentTimeMillis();
        String userJson = String.format("""
                {
                    "login": "%s",
                    "password": "password123",
                    "firstname": "Article",
                    "lastname": "User"
                }
                """, uniqueLogin);

        Long userId = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        
        testUserId = userId;
        return userId;
    }

    private void deleteUser(Long userId) {
        if (userId != null) {
            given()
                    .when()
                    .delete("/api/v1.0/users/" + userId)
                    .then()
                    .statusCode(204);
        }
    }

    @Test
    public void testCreateArticle() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Test Article",
                    "content": "This is a test article content"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Article"))
                .body("content", equalTo("This is a test article content"))
                .body("id", notNullValue());
    }

    @Test
    public void testGetAllArticles() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Test Article 2",
                    "content": "Content 2"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles");

        given()
                .when()
                .get("/api/v1.0/articles")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    public void testGetArticleById() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Test Article 3",
                    "content": "Content 3"
                }
                """, userId);

        Long articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(200)
                .body("id", equalTo(articleId.intValue()))
                .body("title", equalTo("Test Article 3"));
    }

    @Test
    public void testUpdateArticle() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Original Title",
                    "content": "Original Content"
                }
                """, userId);

        Long articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateJson = String.format("""
                {
                    "userId": %d,
                    "title": "Updated Title",
                    "content": "Updated Content"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"))
                .body("content", equalTo("Updated Content"));
    }

    @Test
    public void testDeleteArticle() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "To Delete",
                    "content": "Content"
                }
                """, userId);

        Long articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testConnectArticles() {
        // Check GET Status Code
        given()
                .when()
                .get("/api/v1.0/articles")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetUserByArticleId() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Test Article",
                    "content": "Content"
                }
                """, userId);

        Long articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/articles/" + articleId + "/user")
                .then()
                .statusCode(200)
                .body("id", equalTo(userId.intValue()));

        // Cleanup
        deleteUser(userId);
    }

    @Test
    public void testCreateArticleWithDuplicateTitle() {
        Long userId = createUser();

        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "Duplicate Title",
                    "content": "Content"
                }
                """, userId);

        // Create first article
        given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201);

        // Try to create second article with same title
        given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo("40301"));

        // Cleanup
        deleteUser(userId);
    }

    @Test
    public void testCreateArticleWithInvalidFields() {
        Long userId = createUser();

        // Empty title
        String invalidJson = String.format("""
                {
                    "userId": %d,
                    "title": "",
                    "content": "Content"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Short title
        invalidJson = String.format("""
                {
                    "userId": %d,
                    "title": "x",
                    "content": "Content"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Missing content
        invalidJson = String.format("""
                {
                    "userId": %d,
                    "title": "Valid Title"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Cleanup
        deleteUser(userId);
    }

    @Test
    public void testCreateArticleWithInvalidAssociation() {
        Long userId = createUser();

        // Invalid userId
        String invalidJson = """
                {
                    "userId": 99999,
                    "title": "Test Article",
                    "content": "Content"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));

        // Invalid labelId
        invalidJson = String.format("""
                {
                    "userId": %d,
                    "title": "Test Article",
                    "content": "Content",
                    "labelIds": [99999]
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));

        // Cleanup
        deleteUser(userId);
    }

    @Test
    public void testCreateArticleWithIdInBody() {
        Long userId = createUser();

        String invalidJson = String.format("""
                {
                    "id": 1,
                    "userId": %d,
                    "title": "Test Article",
                    "content": "Content"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Cleanup
        deleteUser(userId);
    }

    @Test
    public void testRegressionCheckRestJpaArticle() {
        Long userId = createUser();

        // CREATE
        String uniqueTitle = "Regression Article " + System.currentTimeMillis();
        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "%s",
                    "content": "Regression Content"
                }
                """, userId, uniqueTitle);

        Long articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .body("title", containsString("Regression Article"))
                .body("content", equalTo("Regression Content"))
                .body("id", notNullValue())
                .extract()
                .path("id");

        // READ by ID
        given()
                .when()
                .get("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(200)
                .body("id", equalTo(articleId.intValue()))
                .body("userId", equalTo(userId.intValue()));

        // READ ALL
        given()
                .when()
                .get("/api/v1.0/articles")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));

        // UPDATE
        String updateJson = String.format("""
                {
                    "userId": %d,
                    "title": "Updated Regression Article",
                    "content": "Updated Content"
                }
                """, userId);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Regression Article"))
                .body("content", equalTo("Updated Content"));

        // DELETE
        given()
                .when()
                .delete("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/articles/" + articleId)
                .then()
                .statusCode(404);

        // Cleanup
        deleteUser(userId);
    }
}

