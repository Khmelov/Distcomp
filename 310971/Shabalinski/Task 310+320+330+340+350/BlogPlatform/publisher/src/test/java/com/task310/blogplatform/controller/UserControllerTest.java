package com.task310.blogplatform.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
public class UserControllerTest {

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

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testCreateUser() {
        String userJson = """
                {
                    "login": "testuser",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .body("login", equalTo("testuser"))
                .body("firstname", equalTo("Test"))
                .body("lastname", equalTo("User"))
                .body("id", notNullValue());
    }

    @Test
    public void testGetAllUsers() {
        // Create a user first
        String userJson = """
                {
                    "login": "testuser2",
                    "password": "password123",
                    "firstname": "Test2",
                    "lastname": "User2"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users");

        given()
                .when()
                .get("/api/v1.0/users")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    public void testGetUserById() {
        // Create a user first
        String userJson = """
                {
                    "login": "testuser3",
                    "password": "password123",
                    "firstname": "Test3",
                    "lastname": "User3"
                }
                """;

        Long userId = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId.intValue()))
                .body("login", equalTo("testuser3"));
    }

    @Test
    public void testUpdateUser() {
        // Create a user first
        String userJson = """
                {
                    "login": "testuser4",
                    "password": "password123",
                    "firstname": "Test4",
                    "lastname": "User4"
                }
                """;

        Long userId = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateJson = """
                {
                    "login": "updateduser",
                    "password": "newpassword",
                    "firstname": "Updated",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/users/" + userId)
                .then()
                .statusCode(200)
                .body("login", equalTo("updateduser"))
                .body("firstname", equalTo("Updated"));
    }

    @Test
    public void testDeleteUser() {
        // Create a user first
        String userJson = """
                {
                    "login": "testuser5",
                    "password": "password123",
                    "firstname": "Test5",
                    "lastname": "User5"
                }
                """;

        Long userId = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1.0/users/" + userId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/users/" + userId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateUserWithInvalidData() {
        String invalidJson = """
                {
                    "login": "",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));
    }

    @Test
    public void testCreateUserWithDuplicateLogin() {
        String userJson = """
                {
                    "login": "duplicatelogin",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        // Create first user
        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201);

        // Try to create second user with same login
        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(403)
                .body("errorCode", equalTo("40301"));
    }

    @Test
    public void testCreateUserWithShortLogin() {
        String invalidJson = """
                {
                    "login": "x",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));
    }

    @Test
    public void testCreateUserWithMissingFields() {
        String invalidJson = """
                {
                    "login": "testuser",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));
    }

    @Test
    public void testCreateUserWithIdInBody() {
        String invalidJson = """
                {
                    "id": 1,
                    "login": "testuser",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("40001"));
    }

    @Test
    public void testRegressionCheckRestUser() {
        // CREATE
        String userJson = """
                {
                    "login": "regressionuser",
                    "password": "password123",
                    "firstname": "Regression",
                    "lastname": "Test"
                }
                """;

        Long userId = given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/api/v1.0/users")
                .then()
                .statusCode(201)
                .body("login", equalTo("regressionuser"))
                .body("firstname", equalTo("Regression"))
                .body("lastname", equalTo("Test"))
                .body("id", notNullValue())
                .extract()
                .path("id");

        // READ by ID
        given()
                .when()
                .get("/api/v1.0/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId.intValue()))
                .body("login", equalTo("regressionuser"));

        // READ ALL
        given()
                .when()
                .get("/api/v1.0/users")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));

        // UPDATE
        String updateJson = """
                {
                    "login": "updatedregression",
                    "password": "newpassword",
                    "firstname": "Updated",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1.0/users/" + userId)
                .then()
                .statusCode(200)
                .body("login", equalTo("updatedregression"))
                .body("firstname", equalTo("Updated"));

        // DELETE
        given()
                .when()
                .delete("/api/v1.0/users/" + userId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1.0/users/" + userId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetUserNotFound() {
        given()
                .when()
                .get("/api/v1.0/users/99999")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("40401"));
    }
}

