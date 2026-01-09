package org.example.task310rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TweetControllerTest extends RestTestBase {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

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

    @Test
    @DisplayName("Check 4xx CREATE with non-valid fields Tweet")
    void createWithInvalidFields() {
        ensureWriter();
        
        // Test with short title
        given().contentType(ContentType.JSON)
                .body(tweetJson("A", "Content content content"))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with short content
        given().contentType(ContentType.JSON)
                .body(tweetJson("Valid Title", "ABC"))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with null writerId
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "writerId": null,
                          "title": "Title",
                          "content": "Content content content"
                        }
                        """)
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Check 4xx CREATE with non-valid association Tweet")
    void createWithInvalidAssociation() {
        ensureWriter();
        
        // Test with non-existent writerId
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "writerId": 99999,
                          "title": "Title",
                          "content": "Content content content"
                        }
                        """)
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.NOT_FOUND.value());

        // Test with non-existent labelId
        given().contentType(ContentType.JSON)
                .body("""
                        {
                          "writerId": %d,
                          "title": "Title",
                          "content": "Content content content",
                          "labelIds": [99999]
                        }
                        """.formatted(writerId))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Check 403 CREATE with duplicate title Tweet")
    void createWithDuplicateTitle() {
        ensureWriter();
        String title = "Duplicate Title";
        
        long tweetId = createTweet(title, "Content content content");
        
        // Try to create another tweet with same title
        given().contentType(ContentType.JSON)
                .body(tweetJson(title, "Different content"))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.FORBIDDEN.value());

        deleteTweet(tweetId);
    }

    @Test
    @DisplayName("Check REST+JPA Tweet (Repeat all CRUD with control in database)")
    void regressionCheckAllCrudWithDatabase() {
        ensureWriter();
        String title = "Regression Tweet";
        String content = "Regression content";
        
        // CREATE
        long tweetId = ((Number) given().contentType(ContentType.JSON)
                .body(tweetJson(title, content))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.CREATED.value())
                .body("title", equalTo(title))
                .body("content", equalTo(content))
                .extract().path("id")).longValue();

        // Verify in database
        if (jdbcTemplate != null) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM distcomp.tbl_tweet WHERE id = ?", 
                    Integer.class, tweetId);
            assertEquals(1, count);
        }

        // READ by ID
        given().when().get("/api/v1.0/tweets/{id}", tweetId)
                .then().statusCode(HttpStatus.OK.value())
                .body("id", equalTo((int) tweetId))
                .body("title", equalTo(title));

        // READ ALL
        given().when().get("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.OK.value())
                .body("size()", notNullValue());

        // UPDATE
        String updatedTitle = "Updated Regression Tweet";
        given().contentType(ContentType.JSON)
                .body(tweetJson(updatedTitle, content))
                .when().put("/api/v1.0/tweets/{id}", tweetId)
                .then().statusCode(HttpStatus.OK.value())
                .body("title", equalTo(updatedTitle));

        // Verify update in database
        if (jdbcTemplate != null) {
            String dbTitle = jdbcTemplate.queryForObject(
                    "SELECT title FROM distcomp.tbl_tweet WHERE id = ?", 
                    String.class, tweetId);
            assertEquals(updatedTitle, dbTitle);
        }

        // DELETE
        given().when().delete("/api/v1.0/tweets/{id}", tweetId)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        // Verify deletion in database
        if (jdbcTemplate != null) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM distcomp.tbl_tweet WHERE id = ?", 
                    Integer.class, tweetId);
            assertEquals(0, count);
        }
    }

    @Test
    @DisplayName("Jdbc Check Label (Wait 3 Labels in Tweet with id=18)")
    void jdbcCheckLabelsInTweet() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should be available");
        ensureWriter();
        
        // Create 3 labels
        long label1Id = createLabel("label1");
        long label2Id = createLabel("label2");
        long label3Id = createLabel("label3");
        
        // Create tweet with 3 labels
        long tweetId = ((Number) given().contentType(ContentType.JSON)
                .body(tweetJsonWithLabels("Tweet with labels", "Content", label1Id, label2Id, label3Id))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        // Verify 3 labels in database for this tweet
        Integer labelCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM distcomp.tbl_tweet_label WHERE tweet_id = ?", 
                Integer.class, tweetId);
        assertEquals(3, labelCount, "Should have 3 labels in tweet");

        // Cleanup
        deleteTweet(tweetId);
        deleteLabel(label1Id);
        deleteLabel(label2Id);
        deleteLabel(label3Id);
    }

    @Test
    @DisplayName("Check Database status (Labels not found)")
    void checkDatabaseStatusLabelsNotFound() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should be available");
        ensureWriter();
        
        // Create labels
        long label1Id = createLabel("db-label1");
        long label2Id = createLabel("db-label2");
        long label3Id = createLabel("db-label3");
        
        // Create tweet with labels
        long tweetId = ((Number) given().contentType(ContentType.JSON)
                .body(tweetJsonWithLabels("DB Test Tweet", "Content", label1Id, label2Id, label3Id))
                .when().post("/api/v1.0/tweets")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();

        // Verify labels exist in database
        Integer countBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM distcomp.tbl_tweet_label WHERE tweet_id = ?", 
                Integer.class, tweetId);
        assertEquals(3, countBefore);

        // Delete tweet (should cascade delete tweet_labels)
        deleteTweet(tweetId);

        // Verify labels are removed from tweet_label table (cascade delete)
        Integer countAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM distcomp.tbl_tweet_label WHERE tweet_id = ?", 
                Integer.class, tweetId);
        assertEquals(0, countAfter, "Labels should be removed after tweet deletion");

        // Labels themselves should still exist
        Integer label1Exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM distcomp.tbl_label WHERE id = ?", 
                Integer.class, label1Id);
        assertEquals(1, label1Exists, "Label should still exist");

        // Cleanup
        deleteLabel(label1Id);
        deleteLabel(label2Id);
        deleteLabel(label3Id);
    }

    private long createLabel(String name) {
        return ((Number) given().contentType(ContentType.JSON)
                .body("""
                        {"name":"%s"}
                        """.formatted(name))
                .when().post("/api/v1.0/labels")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id")).longValue();
    }

    private void deleteLabel(Long id) {
        given().when().delete("/api/v1.0/labels/{id}", id)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String tweetJsonWithLabels(String title, String content, Long... labelIds) {
        StringBuilder labelsJson = new StringBuilder();
        if (labelIds.length > 0) {
            labelsJson.append(",\"labelIds\":[");
            for (int i = 0; i < labelIds.length; i++) {
                if (i > 0) labelsJson.append(",");
                labelsJson.append(labelIds[i]);
            }
            labelsJson.append("]");
        }
        return """
                {
                  "writerId": %d,
                  "title": "%s",
                  "content": "%s"%s
                }
                """.formatted(writerId, title, content, labelsJson.toString());
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


