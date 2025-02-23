package by.kapinskiy.Distcomp;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class IssuesTest extends FunctionalTest {

    private Integer createUserAndGetId() {
        var user = new HashMap<>();
        user.put("login", "user_" + new Random().nextInt(10000));
        user.put("password", "StrongPass123!");
        user.put("firstname", "Test");
        user.put("lastname", "User");

        return given()
                .contentType("application/json")
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private Map<String, Object> createIssue(Integer userId) {
        Map<String, Object> issue = new HashMap<>();
        issue.put("userId", userId);
        issue.put("title", "Valid Title " + new Random().nextInt(10000));
        issue.put("content", "This is a valid content with enough length.");
        return issue;
    }

    @Test
    public void createDuplicateIssueTitle() {
        Integer userId = createUserAndGetId();
        var issue = createIssue(userId);

        given()
                .contentType("application/json")
                .body(issue)
                .when().post("/issues")
                .then()
                .statusCode(201);

        var duplicateIssue = new HashMap<>(issue);

        given()
                .contentType("application/json")
                .body(duplicateIssue)
                .when().post("/issues")
                .then()
                .statusCode(400);
    }

    @Test
    public void createIssueWithInvalidData() {
        Integer userId = createUserAndGetId();
        var issue = createIssue(userId);

        var issueWithShortTitle = new HashMap<>(issue);
        issueWithShortTitle.put("title", "A"); // слишком короткий title

        var issueWithLongTitle = new HashMap<>(issue);
        issueWithLongTitle.put("title", "A".repeat(65)); // слишком длинный title

        var issueWithShortContent = new HashMap<>(issue);
        issueWithShortContent.put("content", "abc"); // слишком короткий content

        var issueWithLongContent = new HashMap<>(issue);
        issueWithLongContent.put("content", "A".repeat(2049)); // слишком длинный content

        var issueWithoutUserId = new HashMap<>(issue);
        issueWithoutUserId.remove("userId"); // userId отсутствует

        given()
                .contentType("application/json")
                .body(issueWithShortTitle)
                .when().post("/issues")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(issueWithLongTitle)
                .when().post("/issues")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(issueWithShortContent)
                .when().post("/issues")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(issueWithLongContent)
                .when().post("/issues")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(issueWithoutUserId)
                .when().post("/issues")
                .then()
                .statusCode(400);
    }
}
