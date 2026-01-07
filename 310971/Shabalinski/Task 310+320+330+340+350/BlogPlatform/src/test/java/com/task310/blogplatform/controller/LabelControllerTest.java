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
public class LabelControllerTest {

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
        String uniqueLogin = "labeluser" + System.currentTimeMillis();
        String userJson = String.format("""
                {
                    "login": "%s",
                    "password": "password123",
                    "firstname": "Label",
                    "lastname": "User"
                }
                """, uniqueLogin);

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
        String uniqueTitle = "Label Article " + System.currentTimeMillis();
        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "%s",
                    "content": "Article Content"
                }
                """, userId, uniqueTitle);

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
    public void testCreateLabel() {
        String labelJson = """
                {
                    "name": "Technology"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(201)
                .body("name", equalTo("Technology"))
                .body("id", notNullValue());
    }

    @Test
    public void testGetAllLabels() {
        String labelJson = """
                {
                    "name": "Science"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels");

        given()
                .when()
                .get("/api/v1.0/labels")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    public void testGetLabelById() {
        String labelJson = """
                {
                    "name": "Sports"
                }
                """;

        Long labelId = given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(200)
                .body("id", equalTo(labelId.intValue()))
                .body("name", equalTo("Sports"));
    }

    @Test
    public void testUpdateLabel() {
        String labelJson = """
                {
                    "name": "Original"
                }
                """;

        Long labelId = given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateJson = """
                {
                    "name": "Updated"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated"));
    }

    @Test
    public void testDeleteLabel() {
        String labelJson = """
                {
                    "name": "To Delete"
                }
                """;

        Long labelId = given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testConnectLabels() {
        // Check GET Status Code
        given()
                .when()
                .get("/api/v1.0/labels")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateLabelWithInvalidData() {
        // Empty name
        String invalidJson = """
                {
                    "name": ""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Missing name
        invalidJson = """
                {
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));

        // Id in body
        invalidJson = """
                {
                    "id": 1,
                    "name": "Test Label"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));
    }

    @Test
    public void testRegressionCheckRestLabel() {
        // CREATE
        String uniqueName = "Regression Label " + System.currentTimeMillis();
        String labelJson = String.format("""
                {
                    "name": "%s"
                }
                """, uniqueName);

        Long labelId = given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(201)
                .body("name", equalTo(uniqueName))
                .body("id", notNullValue())
                .extract()
                .path("id");

        // READ by ID
        given()
                .when()
                .get("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(200)
                .body("id", equalTo(labelId.intValue()))
                .body("name", equalTo(uniqueName));

        // READ ALL
        given()
                .when()
                .get("/api/v1.0/labels")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));

        // UPDATE
        String updatedName = "Updated Regression Label";
        String updateJson = String.format("""
                {
                    "name": "%s"
                }
                """, updatedName);

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(200)
                .body("name", equalTo(updatedName));

        // DELETE
        given()
                .when()
                .delete("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/labels/" + labelId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testLabelArticleAssociation() {
        // Create a label
        String uniqueName = "Test Label " + System.currentTimeMillis();
        String labelJson = String.format("""
                {
                    "name": "%s"
                }
                """, uniqueName);

        Long labelId = given()
                .contentType(ContentType.JSON)
                .body(labelJson)
                .when()
                .post("/api/v1.0/labels")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Create article with label
        String uniqueTitle = "Label Article " + System.currentTimeMillis();
        String articleJson = String.format("""
                {
                    "userId": %d,
                    "title": "%s",
                    "content": "Content",
                    "labelIds": [%d]
                }
                """, testUserId, uniqueTitle, labelId);

        Long articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/v1.0/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Get labels by article ID
        given()
                .when()
                .get("/api/v1.0/articles/" + articleId + "/labels")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class))
                .body("[0].id", equalTo(labelId.intValue()));

        // Cleanup
        given().when().delete("/api/v1.0/articles/" + articleId).then().statusCode(204);
        given().when().delete("/api/v1.0/labels/" + labelId).then().statusCode(204);
    }
}

