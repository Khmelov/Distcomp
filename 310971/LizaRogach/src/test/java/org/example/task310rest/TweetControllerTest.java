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
public class TweetControllerTest extends RestTestBase {

    private Long writerId;

    @Test
    @DisplayName("Connect Tweets (GET status 200)")
    void connect() {
        ensureWriter();
        given().when().get("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Create and delete tweet")
    void createAndDelete() {
        ensureWriter();
        long tweetId = createTweet("Title1", "Content1");
        deleteTweet(tweetId);
    }

    @Test
    @DisplayName("Read tweet")
    void readTweet() {
        ensureWriter();
        long tweetId = createTweet("Title2", "Content2");
        given().when().get("/api/v1.0/tweets/{id}", tweetId)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", equalTo((int) tweetId));
        deleteTweet(tweetId);
    }

    @Test
    @DisplayName("Read tweets list")
    void readAll() {
        ensureWriter();
        long tweetId = createTweet("Title3", "Content3");
        given().when().get("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());
        deleteTweet(tweetId);
    }

    @Test
    @DisplayName("Update tweet")
    void updateTweet() {
        ensureWriter();
        long tweetId = createTweet("Title4", "Content4");
        given().contentType(ContentType.JSON)
                .body(tweetJson("Updated title", "Content4"))
                .when().put("/api/v1.0/tweets/{id}", tweetId)
                .then().statusCode(HttpStatus.OK.value())
                .body("title", equalTo("Updated title"));
        deleteTweet(tweetId);
    }

    @Test
    @DisplayName("Delete tweet and error on second delete")
    void deleteTwice() {
        ensureWriter();
        long tweetId = createTweet("Title5", "Content5");
        deleteTweet(tweetId);
        given().when().delete("/api/v1.0/tweets/{id}", tweetId)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Error update tweet not found")
    void errorUpdateNotFound() {
        ensureWriter();
        given().contentType(ContentType.JSON)
                .body(tweetJson("Any", "Content"))
                .when().put("/api/v1.0/tweets/{id}", 99999)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void ensureWriter() {
        if (writerId == null) {
            writerId = ((Number) given().contentType(ContentType.JSON)
                    .body("""
                            {"login":"tweetwriter@example.com","password":"password1","firstname":"First","lastname":"Last"}
                            """)
                    .when().post("/api/v1.0/writers")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().path("id")).longValue();
        }
    }

    private Long createTweet(String title, String content) {
        return ((Number) given().contentType(ContentType.JSON)
                .body(tweetJson(title, content))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();
    }

    private void deleteTweet(Long id) {
        given().when().delete("/api/v1.0/tweets/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String tweetJson(String title, String content) {
        return """
                {
                  "writerId": %d,
                  "title": "%s",
                  "content": "%s"
                }
                """.formatted(writerId, title, content);
    }
}


