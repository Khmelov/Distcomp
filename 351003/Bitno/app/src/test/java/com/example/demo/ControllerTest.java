package com.example.demo;

<<<<<<< HEAD
//import com.example.labrest.TestcontainersConfig;
import com.example.demo.labrest.dto.CreatorRequestTo;
import com.example.demo.labrest.dto.TopicRequestTo;
=======
import com.example.labrest.dto.CreatorRequestTo;
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
<<<<<<< HEAD
import org.springframework.test.context.ActiveProfiles;

=======
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
<<<<<<< HEAD
//        classes = {TestcontainersConfig.class})
@ActiveProfiles("test")
class ControllerTest {

    @LocalServerPort private int port;

    @BeforeEach
    void setUp() {
=======
public class ControllerTest {
    @LocalServerPort private int port;

    @BeforeEach
    public void setUp() {
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
<<<<<<< HEAD
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
=======
    public void testCreateCreator() {
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c
        CreatorRequestTo req = new CreatorRequestTo("test@test.com", "password123", "John", "Doe");
        given().contentType(ContentType.JSON).body(req).when().post("/api/v1.0/creators")
                .then().statusCode(201).body("login", equalTo("test@test.com"));
    }

    @Test
<<<<<<< HEAD
    void shouldFailValidation() {
=======
    public void testGetCreators() {
        given().when().get("/api/v1.0/creators").then().statusCode(200).body("size()", greaterThan(0));
    }

    @Test
    public void testValidationFail() {
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c
        CreatorRequestTo req = new CreatorRequestTo("a", "pass", "A", "B");
        given().contentType(ContentType.JSON).body(req).when().post("/api/v1.0/creators")
                .then().statusCode(400);
    }
<<<<<<< HEAD

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
=======
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c
}