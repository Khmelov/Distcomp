package com.example.lab.test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostControllerIntegrationTest extends BaseIntegrationTest {

    private static Long postId;

    @Test
    @Order(1)
    @DisplayName("STEP 1: CREATE POST - Should return 201 Created")
    void createPost() {
        String payload = """
                {
                    "content": "This is my first post!",
                    "newsId": 1
                }
                """;

        postId = given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("content", equalTo("This is my first post!"))
                .extract()
                .jsonPath()
                .getLong("id");

        Assertions.assertNotNull(postId);
    }

    @Test
    @Order(2)
    @DisplayName("STEP 2: GET POST BY ID - Should return 200 OK")
    void getPostById() {
        given()
                .baseUri(getBaseUrl())
                .when()
                .get("/api/v1.0/posts/{id}", postId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(postId.intValue()))
                .body("content", equalTo("This is my first post!"));
    }

    @Test
    @Order(3)
    @DisplayName("STEP 3: UPDATE POST - Should return 200 OK")
    void updatePost() {
        String payload = """
                {
                    "id": %d,
                    "content": "This is my updated post!",
                    "newsId": 1
                }
                """.formatted(postId);

        given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1.0/posts/{id}", postId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(postId.intValue()))
                .body("content", equalTo("This is my updated post!"));
    }

    @Test
    @Order(4)
    @DisplayName("STEP 4: DELETE POST - Should return 204 No Content")
    void deletePost() {
        given()
                .baseUri(getBaseUrl())
                .when()
                .delete("/api/v1.0/posts/{id}", postId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(5)
    @DisplayName("STEP 5: GET DELETED POST - Should return 404 Not Found")
    void getDeletedPost() {
        given()
                .baseUri(getBaseUrl())
                .when()
                .get("/api/v1.0/posts/{id}", postId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(40401));
    }

    @Test
    @Order(6)
    @DisplayName("NEGATIVE: CREATE POST WITH EMPTY CONTENT - Should return 400 Bad Request")
    void createPostWithEmptyContent() {
        String payload = """
                {
                    "content": "",
                    "newsId": 1
                }
                """;

        given()
                .baseUri(getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1.0/posts")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}