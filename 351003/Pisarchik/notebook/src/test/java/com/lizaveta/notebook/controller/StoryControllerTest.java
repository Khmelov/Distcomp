package com.lizaveta.notebook.controller;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StoryControllerTest extends PostgresTestContainerConfig {

    @LocalServerPort
    private int port;

    private Long writerId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        writerId = given()
                .contentType(ContentType.JSON)
                .body("{\"writer\":{\"login\":\"story@test.com\",\"password\":\"password12\",\"firstname\":\"Story\",\"lastname\":\"Writer\"}}")
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(201)
                .extract()
                .path("writer.id");
    }

    @Test
    void createStory_ShouldReturn201() {
        String body = String.format("""
                {"story":{"writerId":%d,"title":"Test Story","content":"Some content here"}}
                """, writerId);
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .body("story.id", notNullValue())
                .body("story.title", org.hamcrest.Matchers.equalTo("Test Story"))
                .body("story.writerId", org.hamcrest.Matchers.equalTo(writerId.intValue()))
                .body("story.created", notNullValue())
                .body("story.modified", notNullValue());
    }

    @Test
    void getStories_ShouldReturn200AndPage() {
        given()
                .when()
                .get("/api/v1.0/stories")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("totalElements", notNullValue())
                .body("totalPages", notNullValue());
    }

    @Test
    void getStoryById_WhenExists_ShouldReturn200() {
        String createBody = String.format("""
                {"story":{"writerId":%d,"title":"Get Test","content":"Content for get"}}
                """, writerId);
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .extract()
                .path("story.id");
        given()
                .when()
                .get("/api/v1.0/stories/" + id)
                .then()
                .statusCode(200)
                .body("story.id", org.hamcrest.Matchers.equalTo(id.intValue()))
                .body("story.title", org.hamcrest.Matchers.equalTo("Get Test"));
    }

    @Test
    void updateStory_ShouldReturn200() {
        String createBody = String.format("""
                {"story":{"writerId":%d,"title":"Original","content":"Original content"}}
                """, writerId);
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .extract()
                .path("story.id");
        String updateBody = String.format("""
                {"story":{"writerId":%d,"title":"Updated","content":"Updated content"}}
                """, writerId);
        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1.0/stories/" + id)
                .then()
                .statusCode(200)
                .body("story.title", org.hamcrest.Matchers.equalTo("Updated"));
    }

    @Test
    void deleteStory_ShouldReturn204() {
        String createBody = String.format("""
                {"story":{"writerId":%d,"title":"To Delete","content":"Will be deleted"}}
                """, writerId);
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .extract()
                .path("story.id");
        given()
                .when()
                .delete("/api/v1.0/stories/" + id)
                .then()
                .statusCode(204);
        given()
                .when()
                .get("/api/v1.0/stories/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void getWriterByStoryId_ShouldReturn200() {
        String createBody = String.format("""
                {"story":{"writerId":%d,"title":"Writer Test","content":"Content"}}
                """, writerId);
        Long storyId = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .extract()
                .path("story.id");
        given()
                .when()
                .get("/api/v1.0/stories/" + storyId + "/writer")
                .then()
                .statusCode(200)
                .body("writer.id", org.hamcrest.Matchers.equalTo(writerId.intValue()))
                .body("writer.login", org.hamcrest.Matchers.equalTo("story@test.com"));
    }

    @Test
    void getNoticesByStoryId_ShouldReturn200() {
        String createBody = String.format("""
                {"story":{"writerId":%d,"title":"Notice Test","content":"Content"}}
                """, writerId);
        Long storyId = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .extract()
                .path("story.id");
        given()
                .when()
                .get("/api/v1.0/stories/" + storyId + "/notices")
                .then()
                .statusCode(200);
    }
}
