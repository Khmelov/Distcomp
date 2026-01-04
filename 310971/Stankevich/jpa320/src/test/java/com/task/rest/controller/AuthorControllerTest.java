package com.task.rest.controller;

import com.task.rest.BaseIntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthorControllerTest extends BaseIntegrationTest {

    @Test
    void testCreateAuthor_Success() {
        String requestBody = """
            {
                "login": "testauthor",
                "password": "password123",
                "firstname": "John",
                "lastname": "Doe"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("login", equalTo("testauthor"))
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"))
                .body("id", notNullValue());
    }

    @Test
    void testCreateAuthor_InvalidPassword() {
        String requestBody = """
            {
                "login": "testauthor2",
                "password": "short",
                "firstname": "Jane",
                "lastname": "Smith"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorMessage", containsString("Password must be between 8 and 128 characters"));
    }

    @Test
    void testCreateAuthor_InvalidFirstname() {
        String requestBody = """
            {
                "login": "testauthor3",
                "password": "password123",
                "firstname": "x",
                "lastname": "Johnson"
            }
            """;

        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorMessage", containsString("Firstname must be between 2 and 64 characters"));
    }

    @Test
    void testCreateAuthor_DuplicateLogin() {
        String requestBody = """
            {
                "login": "duplicateuser",
                "password": "password123",
                "firstname": "First",
                "lastname": "Last"
            }
            """;

        // Create first author
        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Try to create duplicate
        given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("errorMessage", containsString("Author with login already exists"));
    }

    @Test
    void testGetAuthorById_Success() {
        // Create author first
        String requestBody = """
            {
                "login": "getbyid",
                "password": "password123",
                "firstname": "Get",
                "lastname": "ById"
            }
            """;

        Integer authorId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Get author by id
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/authors/{id}", authorId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(authorId))
                .body("login", equalTo("getbyid"));
    }

    @Test
    void testGetAuthorById_NotFound() {
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/authors/{id}", 99999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorMessage", containsString("Author not found"));
    }

    @Test
    void testGetAllAuthors_Success() {
        // Create multiple authors
        for (int i = 1; i <= 3; i++) {
            String requestBody = String.format("""
                {
                    "login": "author%d",
                    "password": "password123",
                    "firstname": "First%d",
                    "lastname": "Last%d"
                }
                """, i, i, i);

            given()
                    .spec(requestSpec)
                    .body(requestBody)
                    .when()
                    .post("/api/v1.0/authors");
        }

        // Get all authors
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(3));
    }

    @Test
    void testUpdateAuthor_Success() {
        // Create author
        String createBody = """
            {
                "login": "updatetest",
                "password": "password123",
                "firstname": "Old",
                "lastname": "Name"
            }
            """;

        Integer authorId = given()
                .spec(requestSpec)
                .body(createBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Update author
        String updateBody = """
            {
                "login": "updatetest",
                "password": "newpassword123",
                "firstname": "New",
                "lastname": "Name"
            }
            """;

        given()
                .spec(requestSpec)
                .body(updateBody)
                .when()
                .put("/api/v1.0/authors/{id}", authorId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("firstname", equalTo("New"))
                .body("lastname", equalTo("Name"));
    }

    @Test
    void testDeleteAuthor_Success() {
        // Create author
        String requestBody = """
            {
                "login": "deletetest",
                "password": "password123",
                "firstname": "Delete",
                "lastname": "Me"
            }
            """;

        Integer authorId = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/api/v1.0/authors")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        // Delete author
        given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1.0/authors/{id}", authorId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Verify author is deleted
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1.0/authors/{id}", authorId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDeleteAuthor_NotFound() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1.0/authors/{id}", 99999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
