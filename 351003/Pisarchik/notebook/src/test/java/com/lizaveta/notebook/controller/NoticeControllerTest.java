package com.lizaveta.notebook.controller;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoticeControllerTest extends PostgresTestContainerConfig {

    @LocalServerPort
    private int port;

    private Long storyId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        Long writerId = given()
                .contentType(ContentType.JSON)
                .body("{\"writer\":{\"login\":\"notice@test.com\",\"password\":\"password12\",\"firstname\":\"Notice\",\"lastname\":\"Writer\"}}")
                .when()
                .post("/api/v1.0/writers")
                .then()
                .statusCode(201)
                .extract()
                .path("writer.id");
        storyId = given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"story\":{\"writerId\":%d,\"title\":\"Notice Story\",\"content\":\"Content for notices\"}}", writerId))
                .when()
                .post("/api/v1.0/stories")
                .then()
                .statusCode(201)
                .extract()
                .path("story.id");
    }

    @Test
    void createNotice_ShouldReturn201() {
        String body = String.format("""
                {"notice":{"storyId":%d,"content":"A notice comment"}}
                """, storyId);
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(201)
                .body("notice.id", notNullValue())
                .body("notice.storyId", equalTo(storyId.intValue()))
                .body("notice.content", equalTo("A notice comment"));
    }

    @Test
    void getNotices_ShouldReturn200AndPage() {
        given()
                .when()
                .get("/api/v1.0/notices")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("totalElements", notNullValue())
                .body("totalPages", notNullValue());
    }

    @Test
    void getNoticeById_WhenExists_ShouldReturn200() {
        String createBody = String.format("""
                {"notice":{"storyId":%d,"content":"Get notice test"}}
                """, storyId);
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(201)
                .extract()
                .path("notice.id");
        given()
                .when()
                .get("/api/v1.0/notices/" + id)
                .then()
                .statusCode(200)
                .body("notice.id", equalTo(id.intValue()))
                .body("notice.content", equalTo("Get notice test"));
    }

    @Test
    void updateNotice_ShouldReturn200() {
        String createBody = String.format("""
                {"notice":{"storyId":%d,"content":"Original notice"}}
                """, storyId);
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(201)
                .extract()
                .path("notice.id");
        String updateBody = String.format("""
                {"notice":{"storyId":%d,"content":"Updated notice"}}
                """, storyId);
        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/v1.0/notices/" + id)
                .then()
                .statusCode(200)
                .body("notice.content", equalTo("Updated notice"));
    }

    @Test
    void deleteNotice_ShouldReturn204() {
        String createBody = String.format("""
                {"notice":{"storyId":%d,"content":"To delete"}}
                """, storyId);
        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(201)
                .extract()
                .path("notice.id");
        given()
                .when()
                .delete("/api/v1.0/notices/" + id)
                .then()
                .statusCode(204);
        given()
                .when()
                .get("/api/v1.0/notices/" + id)
                .then()
                .statusCode(404);
    }
}
