package org.example;

import io.restassured.http.ContentType;
import org.example.dto.EditorRequestTo;
import org.example.dto.NewsRequestTo;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

public class NewsControllerTest extends BaseTest {

    @Test
    void shouldCreateNewsAndCheckPagination() {
        // 1. Сначала создаем автора
        EditorRequestTo editor = new EditorRequestTo(null, "news_maker", "password123", "Leo", "Tolstoy");
        Long editorId = given()
                .contentType(ContentType.JSON)
                .body(editor)
                .post("/editors")
                .then().extract().path("id");

        // 2. Создаем новость
        NewsRequestTo news = new NewsRequestTo(null, editorId, "Great Title", "Some long content for news");
        given()
                .contentType(ContentType.JSON)
                .body(news)
                .post("/news")
                .then()
                .statusCode(201);

        // 3. Проверяем пагинацию (findAll)
        given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/news")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }
}