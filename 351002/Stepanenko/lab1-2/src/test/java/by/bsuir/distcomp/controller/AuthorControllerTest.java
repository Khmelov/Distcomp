package by.bsuir.distcomp.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorControllerTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void testCreateAuthor() {
        String requestBody = """
                {
                    "login": "testuser",
                    "password": "password123",
                    "firstname": "Test",
                    "lastname": "User"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("login", equalTo("testuser"))
                .body("firstname", equalTo("Test"))
                .body("lastname", equalTo("User"))
                .body("password", nullValue());
    }

    @Test
    void testGetAuthorById() {
        String requestBody = """
                {
                    "login": "getuser",
                    "password": "password123",
                    "firstname": "Get",
                    "lastname": "User"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .get("/api/v1.0/authors/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("login", equalTo("getuser"));
    }

    @Test
    void testGetAllAuthors() {
        given()
        .when()
                .get("/api/v1.0/authors")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testUpdateAuthor() {
        String createBody = """
                {
                    "login": "updateuser",
                    "password": "password123",
                    "firstname": "Update",
                    "lastname": "User"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateBody = """
                {
                    "id": %d,
                    "login": "updateduser",
                    "password": "newpassword123",
                    "firstname": "Updated",
                    "lastname": "User"
                }
                """.formatted(id);

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
        .when()
                .put("/api/v1.0/authors")
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("login", equalTo("updateduser"))
                .body("firstname", equalTo("Updated"));
    }

    @Test
    void testDeleteAuthor() {
        String requestBody = """
                {
                    "login": "deleteuser",
                    "password": "password123",
                    "firstname": "Delete",
                    "lastname": "User"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .delete("/api/v1.0/authors/" + id)
        .then()
                .statusCode(204);

        given()
        .when()
                .get("/api/v1.0/authors/" + id)
        .then()
                .statusCode(404);
    }

    @Test
    void testCreateAuthorValidation() {
        String requestBody = """
                {
                    "login": "",
                    "password": "123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(400);
    }

    @Test
    void testGetAuthorNotFound() {
        given()
        .when()
                .get("/api/v1.0/authors/99999")
        .then()
                .statusCode(404);
    }
}
