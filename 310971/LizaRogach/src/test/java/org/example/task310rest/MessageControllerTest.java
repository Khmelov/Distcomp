package org.example.task310rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageControllerTest extends RestTestBase {

    private Long writerId;
    private Long tweetId;

    @Test
    @DisplayName("Connect Messages (GET status 200)")
    void connect() {
        ensureRefs();
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Create and delete message")
    void createAndDelete() {
        ensureRefs();
        long messageId = createMessage("Hello world");
        deleteMessage(messageId);
    }

    @Test
    @DisplayName("Read message")
    void readMessage() {
        ensureRefs();
        long id = createMessage("Read content");
        given().when().get("/api/v1.0/messages/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", equalTo((int) id));
        deleteMessage(id);
    }

    @Test
    @DisplayName("Read messages list")
    void readAll() {
        ensureRefs();
        long id = createMessage("List content");
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());
        deleteMessage(id);
    }

    @Test
    @DisplayName("Update message")
    void updateMessage() {
        ensureRefs();
        long id = createMessage("Old content");
        given().contentType(ContentType.JSON)
                .body(messageJson("New content"))
                .when().put("/api/v1.0/messages/{id}", id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo("New content"));
        deleteMessage(id);
    }

    @Test
    @DisplayName("Delete message and error on second delete")
    void deleteTwice() {
        ensureRefs();
        long id = createMessage("Delete me");
        deleteMessage(id);
        given().when().delete("/api/v1.0/messages/{id}", id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Error update message not found")
    void errorUpdateNotFound() {
        ensureRefs();
        given().contentType(ContentType.JSON)
                .body(messageJson("Any"))
                .when().put("/api/v1.0/messages/{id}", 99999)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void ensureRefs() {
        if (writerId == null) {
            writerId = ((Number) given().contentType(ContentType.JSON)
                    .body("""
                            {"login":"messagewriter@example.com","password":"password1","firstname":"Msg","lastname":"Writer"}
                            """)
                    .when().post("/api/v1.0/writers")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().path("id")).longValue();
        }
        if (tweetId == null) {
            tweetId = ((Number) given().contentType(ContentType.JSON)
                    .body("""
                            {"writerId":%d,"title":"Msg title","content":"Msg content"}
                            """.formatted(writerId))
                    .when().post("/api/v1.0/tweets")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().path("id")).longValue();
        }
    }

    private long createMessage(String content) {
        return ((Number) given().contentType(ContentType.JSON)
                .body(messageJson(content))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();
    }

    private void deleteMessage(Long id) {
        given().when().delete("/api/v1.0/messages/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String messageJson(String content) {
        return """
                {"tweetId":%d,"content":"%s"}
                """.formatted(tweetId, content);
    }
}


