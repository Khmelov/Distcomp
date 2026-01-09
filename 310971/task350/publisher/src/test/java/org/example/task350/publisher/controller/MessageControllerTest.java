package org.example.task350.publisher.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import org.example.task350.publisher.RestTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageControllerTest extends RestTestBase {

    private static final String DEFAULT_COUNTRY = "default";
    private Long writerId;
    private Long tweetId;

    // Override discussion service URL to point to actual running service
    // In real tests, this should be configured to point to discussion module
    @DynamicPropertySource
    static void configureDiscussionService(DynamicPropertyRegistry registry) {
        // Use actual discussion service URL if available, otherwise tests will fail
        // This assumes discussion module is running on port 24130
        registry.add("discussion.service.url", () -> "http://localhost:24130");
    }

    @org.junit.jupiter.api.BeforeEach
    void ensureRefs() {
        if (writerId == null) {
            try {
                // Create writer
                String login = "testwriter" + System.currentTimeMillis();
                writerId = ((Number) given().contentType(ContentType.JSON)
                        .body("""
                                {
                                  "login": "%s",
                                  "password": "password123",
                                  "firstname": "Test",
                                  "lastname": "Writer"
                                }
                                """.formatted(login))
                        .when().post("/api/v1.0/writers")
                        .then().statusCode(HttpStatus.CREATED.value())
                        .extract().path("id")).longValue();
                
                // Create tweet
                tweetId = ((Number) given().contentType(ContentType.JSON)
                        .body("""
                                {
                                  "writerId": %d,
                                  "title": "Test Tweet %s",
                                  "content": "Test content for message tests"
                                }
                                """.formatted(writerId, System.currentTimeMillis()))
                        .when().post("/api/v1.0/tweets")
                        .then().statusCode(HttpStatus.CREATED.value())
                        .extract().path("id")).longValue();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create test data: " + e.getMessage(), e);
            }
        }
    }

    @Test
    @DisplayName("Connect Messages (Check public REST GET Status Code)")
    void connectPublicRest() {
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Create message via public REST")
    void createMessage() {
        String content = "Public REST test content";
        Long id = ((Number) given().contentType(ContentType.JSON)
                .body(messageJson(content))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("content", equalTo(content))
                .extract().path("id")).longValue();
        
        // Cleanup
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
    }

    @Test
    @DisplayName("Read message via public REST")
    void readMessage() {
        // Create message first
        String content = "Read via public REST";
        Long id = createMessage(content);
        
        // Read via public REST
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("content", equalTo(content));
        
        // Cleanup
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
    }

    @Test
    @DisplayName("Read messages list via public REST")
    void readAll() {
        String content = "List via public REST";
        Long id = createMessage(content);
        
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());
        
        // Cleanup
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
    }

    @Test
    @DisplayName("Update message via public REST")
    void updateMessage() {
        String oldContent = "Old content";
        String newContent = "New content";
        Long id = createMessage(oldContent);
        
        given().contentType(ContentType.JSON)
                .body(messageJsonForUpdate(newContent, DEFAULT_COUNTRY))
                .when().put("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                        DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo(newContent));
        
        // Cleanup
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
    }

    @Test
    @DisplayName("Delete message via public REST")
    void deleteMessage() {
        Long id = createMessage("Delete me");
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
        
        // Verify deletion
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Double endpoints check REST Message")
    void doubleEndpointsCheck() {
        String content = "Double endpoints test";
        Long id = createMessage(content);
        
        // Check via public REST (publisher)
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo(content));
        
        // Check via inner REST (discussion) - direct call
        // Note: This requires discussion module to be accessible
        // In a real scenario, you might want to use a different approach
        given().baseUri("http://localhost:24130")
                .when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                        DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo(content));
        
        // Cleanup
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
    }

    @Test
    @DisplayName("Regression check REST Message (Repeat all CRUD via public REST)")
    void regressionCheckAllCrud() {
        // CREATE
        String content = "Regression test content";
        Long id = createMessage(content);
        
        // READ by ID
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo(content));
        
        // READ all
        given().when().get("/api/v1.0/messages")
                .then().statusCode(HttpStatus.OK.value());
        
        // UPDATE
        String updatedContent = "Updated regression content";
        given().contentType(ContentType.JSON)
                .body(messageJsonForUpdate(updatedContent, DEFAULT_COUNTRY))
                .when().put("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                        DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.OK.value())
                .body("content", equalTo(updatedContent));
        
        // DELETE
        deleteMessage(DEFAULT_COUNTRY, tweetId, id);
        
        // Verify DELETE
        given().when().get("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                DEFAULT_COUNTRY, tweetId, id)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    private Long createMessage(String content) {
        return ((Number) given().contentType(ContentType.JSON)
                .body(messageJson(content))
                .when().post("/api/v1.0/messages")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();
    }

    private void deleteMessage(String country, Long tweetId, Long id) {
        given().when().delete("/api/v1.0/messages/{country}/{tweetId}/{id}", 
                country, tweetId, id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String messageJson(String content) {
        return """
                {
                  "tweetId": %d,
                  "content": "%s"
                }
                """.formatted(tweetId, content);
    }

    private String messageJsonForUpdate(String content, String country) {
        return """
                {
                  "country": "%s",
                  "tweetId": %d,
                  "content": "%s"
                }
                """.formatted(country, tweetId, content);
    }
}

