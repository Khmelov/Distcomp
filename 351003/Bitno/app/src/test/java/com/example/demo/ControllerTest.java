package com.example.demo;

//import com.example.labrest.TestcontainersConfig;
import com.example.demo.labrest.dto.CreatorRequestTo;
import com.example.demo.labrest.dto.TopicRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//        classes = {TestcontainersConfig.class})
@ActiveProfiles("test")
class ControllerTest {

    @LocalServerPort private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void shouldGetInitialCreator() {
        given().when().get("/api/v1.0/creators")
                .then().statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].login", equalTo("misabitnol@gmail.com"))
                .body("[0].firstname", equalTo("Михаил"))
                .body("[0].lastname", equalTo("Битно"));
    }

    @Test
    void shouldCreateCreator() {
        CreatorRequestTo req = new CreatorRequestTo("test@test.com", "password123", "John", "Doe");
        given().contentType(ContentType.JSON).body(req).when().post("/api/v1.0/creators")
                .then().statusCode(201).body("login", equalTo("test@test.com"));
    }

    @Test
    void shouldFailValidation() {
        CreatorRequestTo req = new CreatorRequestTo("a", "pass", "A", "B");
        given().contentType(ContentType.JSON).body(req).when().post("/api/v1.0/creators")
                .then().statusCode(400);
    }

    @Test
    void shouldDeleteCreator() {
        CreatorRequestTo req = new CreatorRequestTo("del@test.com", "password123", "Del", "User");
        Long id = given()
                .contentType(ContentType.JSON)
                .body(req)
                .when()
                .post("/api/v1.0/creators")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1.0/creators/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1.0/creators/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldGetTopicByCreator() {
        CreatorRequestTo creatorReq = new CreatorRequestTo("topicuser@test.com", "pass123", "Topic", "User");
        Long creatorId = given()
                .contentType(ContentType.JSON)
                .body(creatorReq)
                .when()
                .post("/api/v1.0/creators")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        TopicRequestTo topicReq = new TopicRequestTo(creatorId, "Some title", "Some content", null, null);
        Long topicId = given()
                .contentType(ContentType.JSON)
                .body(topicReq)
                .when()
                .post("/api/v1.0/topics")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/topics/" + topicId + "/creator")
                .then()
                .statusCode(200)
                .body("login", equalTo("topicuser@test.com"));
    }

    @Test
    void shouldCreateAndGetTopic() {
        String topicJson = """
            {"creatorId": 1, "title": "Test Topic", "content": "Test content here", "markerIds": []}
            """;
        given().contentType(ContentType.JSON).body(topicJson).when().post("/api/v1.0/topics")
                .then().statusCode(201).body("title", equalTo("Test Topic"));
    }
}