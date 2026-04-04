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
class MarkerControllerTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void testCreateMarker() {
        String requestBody = """
                {
                    "name": "test-marker"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/markers")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("test-marker"));
    }

    @Test
    void testGetMarkerById() {
        String createBody = """
                {
                    "name": "get-marker"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/markers")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .get("/api/v1.0/markers/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("get-marker"));
    }

    @Test
    void testGetAllMarkers() {
        given()
        .when()
                .get("/api/v1.0/markers")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    void testUpdateMarker() {
        String createBody = """
                {
                    "name": "update-marker"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/markers")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateBody = """
                {
                    "id": %d,
                    "name": "updated-marker"
                }
                """.formatted(id);

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
        .when()
                .put("/api/v1.0/markers")
        .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("updated-marker"));
    }

    @Test
    void testDeleteMarker() {
        String createBody = """
                {
                    "name": "delete-marker"
                }
                """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
        .when()
                .post("/api/v1.0/markers")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .delete("/api/v1.0/markers/" + id)
        .then()
                .statusCode(204);

        given()
        .when()
                .get("/api/v1.0/markers/" + id)
        .then()
                .statusCode(404);
    }

    @Test
    void testGetMarkersByTweetId() {
        String authorBody = """
                {
                    "login": "markerauthor",
                    "password": "password123",
                    "firstname": "Marker",
                    "lastname": "Author"
                }
                """;

        Long authorId = given()
                .contentType(ContentType.JSON)
                .body(authorBody)
        .when()
                .post("/api/v1.0/authors")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String markerBody = """
                {
                    "name": "tweet-marker"
                }
                """;

        Long markerId = given()
                .contentType(ContentType.JSON)
                .body(markerBody)
        .when()
                .post("/api/v1.0/markers")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        String tweetBody = """
                {
                    "authorId": %d,
                    "title": "Tweet with Marker",
                    "content": "Content",
                    "markerIds": [%d]
                }
                """.formatted(authorId, markerId);

        Long tweetId = given()
                .contentType(ContentType.JSON)
                .body(tweetBody)
        .when()
                .post("/api/v1.0/tweets")
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
                .get("/api/v1.0/markers/tweet/" + tweetId)
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("size()", greaterThan(0));
    }

    @Test
    void testCreateMarkerValidation() {
        String requestBody = """
                {
                    "name": ""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1.0/markers")
        .then()
                .statusCode(400);
    }

    @Test
    void testGetMarkerNotFound() {
        given()
        .when()
                .get("/api/v1.0/markers/99999")
        .then()
                .statusCode(404);
    }
}
