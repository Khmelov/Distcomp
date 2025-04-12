package by.molchan.Publisher;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ArticlesTest extends FunctionalTest {

    private Integer createCreatorAndGetId() {
        var creator = new HashMap<>();
        creator.put("login", "creator_" + new Random().nextInt(10000));
        creator.put("password", "StrongPass123!");
        creator.put("firstname", "Test");
        creator.put("lastname", "Creator");

        return given()
                .contentType("application/json")
                .body(creator)
                .when().post("/creators")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private Map<String, Object> createArticle(Integer creatorId) {
        Map<String, Object> article = new HashMap<>();
        article.put("creatorId", creatorId);
        article.put("title", "Valid Title " + new Random().nextInt(10000));
        article.put("content", "This is a valid content with enough length.");
        return article;
    }

    @Test
    public void createDuplicateArticleTitle() {
        Integer creatorId = createCreatorAndGetId();
        var article = createArticle(creatorId);

        given()
                .contentType("application/json")
                .body(article)
                .when().post("/articles")
                .then()
                .statusCode(201);

        var duplicateArticle = new HashMap<>(article);

        given()
                .contentType("application/json")
                .body(duplicateArticle)
                .when().post("/articles")
                .then()
                .statusCode(400);
    }

    @Test
    public void createArticleWithInvalidData() {
        Integer creatorId = createCreatorAndGetId();
        var article = createArticle(creatorId);

        var articleWithShortTitle = new HashMap<>(article);
        articleWithShortTitle.put("title", "A"); // слишком короткий title

        var articleWithLongTitle = new HashMap<>(article);
        articleWithLongTitle.put("title", "A".repeat(65)); // слишком длинный title

        var articleWithShortContent = new HashMap<>(article);
        articleWithShortContent.put("content", "abc"); // слишком короткий content

        var articleWithLongContent = new HashMap<>(article);
        articleWithLongContent.put("content", "A".repeat(2049)); // слишком длинный content

        var articleWithoutCreatorId = new HashMap<>(article);
        articleWithoutCreatorId.remove("creatorId"); // creatorId отсутствует

        given()
                .contentType("application/json")
                .body(articleWithShortTitle)
                .when().post("/articles")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(articleWithLongTitle)
                .when().post("/articles")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(articleWithShortContent)
                .when().post("/articles")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(articleWithLongContent)
                .when().post("/articles")
                .then()
                .statusCode(400);

        given()
                .contentType("application/json")
                .body(articleWithoutCreatorId)
                .when().post("/articles")
                .then()
                .statusCode(400);
    }
}
