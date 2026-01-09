package org.example.task330.discussion;

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

    private static final String DEFAULT_COUNTRY = "default";
    private Long tweetId = 1L;
    private String country;
    private Long messageId;

    @Test
    @DisplayName("Connect Messages (Check inner REST GET Status Code)")
    void connectInnerRest() {
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Create and delete message")
    void createAndDelete() {
        MessageData data = createMessage("Hello world");
        deleteMessage(data.country, data.tweetId, data.id);
    }

    @Test
    @DisplayName("Read message")
    void readMessage() {
        MessageData data = createMessage("Read content");
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                data.country, data.tweetId, data.id)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("content", equalTo("Read content"));
        deleteMessage(data.country, data.tweetId, data.id);
    }

    @Test
    @DisplayName("Read messages list")
    void readAll() {
        MessageData data = createMessage("List content");
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());
        deleteMessage(data.country, data.tweetId, data.id);
    }

    @Test
    @DisplayName("Read messages by country and tweetId")
    void readByCountryAndTweetId() {
        String testCountry = "test-country";
        MessageData data = createMessage("Filtered content", testCountry);
        given().when()
                .queryParam("country", testCountry)
                .queryParam("tweetId", data.tweetId)
                .get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());
        deleteMessage(data.country, data.tweetId, data.id);
    }

    @Test
    @DisplayName("Update message")
    void updateMessage() {
        MessageData data = createMessage("Old content");
        given().contentType(ContentType.JSON)
                .body(messageJson("New content", data.country))
                .when().put("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                        data.country, data.tweetId, data.id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo("New content"));
        deleteMessage(data.country, data.tweetId, data.id);
    }

    @Test
    @DisplayName("Delete message and error on second delete")
    void deleteTwice() {
        MessageData data = createMessage("Delete me");
        deleteMessage(data.country, data.tweetId, data.id);
        given().when().delete("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                data.country, data.tweetId, data.id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Error update message not found")
    void errorUpdateNotFound() {
        given().contentType(ContentType.JSON)
                .body(messageJson("Any", DEFAULT_COUNTRY))
                .when().put("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                        DEFAULT_COUNTRY, 99999L, 99999L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Error get message not found")
    void errorGetNotFound() {
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                DEFAULT_COUNTRY, 99999L, 99999L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Check 4xx CREATE with non-valid fields Message")
    void createWithInvalidFields() {
        // Test with empty content
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "country": "%s",
                          "tweetId": %d,
                          "content": ""
                        }
                        """.formatted(DEFAULT_COUNTRY, tweetId))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with short content
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "country": "%s",
                          "tweetId": %d,
                          "content": "A"
                        }
                        """.formatted(DEFAULT_COUNTRY, tweetId))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with null tweetId
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "country": "%s",
                          "tweetId": null,
                          "content": "Valid content"
                        }
                        """.formatted(DEFAULT_COUNTRY))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Regression check REST Message (Repeat all CRUD)")
    void regressionCheckAllCrud() {
        // CREATE
        MessageData data = createMessage("Regression test content");
        
        // READ by ID
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                data.country, data.tweetId, data.id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo("Regression test content"));
        
        // READ all
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value());
        
        // UPDATE
        given().contentType(ContentType.JSON)
                .body(messageJson("Updated regression content", data.country))
                .when().put("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                        data.country, data.tweetId, data.id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo("Updated regression content"));
        
        // DELETE
        deleteMessage(data.country, data.tweetId, data.id);
        
        // Verify DELETE
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                data.country, data.tweetId, data.id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    private MessageData createMessage(String content) {
        return createMessage(content, DEFAULT_COUNTRY);
    }

    private MessageData createMessage(String content, String country) {
        Long id = ((Number) given().contentType(ContentType.JSON)
                .body(messageJson(content, country))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();
        
        return new MessageData(country, tweetId, id);
    }

    private void deleteMessage(String country, Long tweetId, Long id) {
        given().when().delete("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                country, tweetId, id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String messageJson(String content, String country) {
        return """
                {
                  "country": "%s",
                  "tweetId": %d,
                  "content": "%s"
                }
                """.formatted(country, tweetId, content);
    }

    private static class MessageData {
        final String country;
        final Long tweetId;
        final Long id;

        MessageData(String country, Long tweetId, Long id) {
            this.country = country;
            this.tweetId = tweetId;
            this.id = id;
        }
    }
}

