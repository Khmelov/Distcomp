package by.bsuir.task310;

import by.bsuir.task310.repository.ArticleRepository;
import by.bsuir.task310.repository.CreatorRepository;
import by.bsuir.task310.repository.NoticeRepository;
import by.bsuir.task310.repository.StickerRepository;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    CreatorRepository creatorRepository;
    @Autowired
    StickerRepository stickerRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    NoticeRepository noticeRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        noticeRepository.clear();
        articleRepository.clear();
        stickerRepository.clear();
        creatorRepository.clear();
    }

    @Test
    void fullCrudFlowShouldWork() {
        Long creatorId =
                given()
                        .contentType("application/json")
                        .body("""
                            {
                              "creator": {
                                "login": "alex@example.com",
                                "password": "password123",
                                "firstname": "Александра",
                                "lastname": "Левенталь"
                              }
                            }
                            """)
                        .when()
                        .post("/api/v1.0/creators")
                        .then()
                        .statusCode(201)
                        .body("creator.login", Matchers.equalTo("alex@example.com"))
                        .extract()
                        .path("creator.id");

        Long stickerId =
                given()
                        .contentType("application/json")
                        .body("""
                            {
                              "sticker": {
                                "name": "java"
                              }
                            }
                            """)
                        .when()
                        .post("/api/v1.0/stickers")
                        .then()
                        .statusCode(201)
                        .body("sticker.name", Matchers.equalTo("java"))
                        .extract()
                        .path("sticker.id");

        Long articleId =
                given()
                        .contentType("application/json")
                        .body(String.format("""
                            {
                              "article": {
                                "creatorId": %d,
                                "title": "REST basics",
                                "content": "Some useful REST content",
                                "stickerIds": [%d]
                              }
                            }
                            """, creatorId, stickerId))
                        .when()
                        .post("/api/v1.0/articles")
                        .then()
                        .statusCode(201)
                        .body("article.title", Matchers.equalTo("REST basics"))
                        .extract()
                        .path("article.id");

        Long noticeId =
                given()
                        .contentType("application/json")
                        .body(String.format("""
                            {
                              "notice": {
                                "articleId": %d,
                                "content": "Very important note"
                              }
                            }
                            """, articleId))
                        .when()
                        .post("/api/v1.0/notices")
                        .then()
                        .statusCode(201)
                        .body("notice.content", Matchers.equalTo("Very important note"))
                        .extract()
                        .path("notice.id");

        given()
                .when()
                .get("/api/v1.0/articles/{id}", articleId)
                .then()
                .statusCode(200)
                .body("article.creatorId", Matchers.equalTo(creatorId.intValue()))
                .body("article.stickerIds[0]", Matchers.equalTo(stickerId.intValue()));

        given()
                .when()
                .get("/api/v1.0/creators/byArticle/{articleId}", articleId)
                .then()
                .statusCode(200)
                .body("creator.id", Matchers.equalTo(creatorId.intValue()));

        given()
                .when()
                .get("/api/v1.0/stickers/byArticle/{articleId}", articleId)
                .then()
                .statusCode(200)
                .body("size()", Matchers.equalTo(1));

        given()
                .when()
                .get("/api/v1.0/notices/byArticle/{articleId}", articleId)
                .then()
                .statusCode(200)
                .body("size()", Matchers.equalTo(1));

        given()
                .contentType("application/json")
                .body(String.format("""
                    {
                      "article": {
                        "creatorId": %d,
                        "title": "REST advanced",
                        "content": "Updated article content",
                        "stickerIds": [%d]
                      }
                    }
                    """, creatorId, stickerId))
                .when()
                .put("/api/v1.0/articles/{id}", articleId)
                .then()
                .statusCode(200)
                .body("article.title", Matchers.equalTo("REST advanced"));

        given()
                .when()
                .get("/api/v1.0/articles?creatorLogin=alex@example.com&stickerName=java&title=REST")
                .then()
                .statusCode(200)
                .body("size()", Matchers.equalTo(1));

        given()
                .when()
                .delete("/api/v1.0/notices/{id}", noticeId)
                .then()
                .statusCode(204);

        given()
                .when()
                .delete("/api/v1.0/articles/{id}", articleId)
                .then()
                .statusCode(204);

        given()
                .when()
                .delete("/api/v1.0/stickers/{id}", stickerId)
                .then()
                .statusCode(204);

        given()
                .when()
                .delete("/api/v1.0/creators/{id}", creatorId)
                .then()
                .statusCode(204);
    }

    @Test
    void validationErrorShouldReturn4xxAndFiveDigitCode() {
        given()
                .contentType("application/json")
                .body("""
                    {
                      "creator": {
                        "login": "",
                        "password": "123",
                        "firstname": "A",
                        "lastname": "B"
                      }
                    }
                    """)
                .when()
                .post("/api/v1.0/creators")
                .then()
                .statusCode(400)
                .body("errorMessage", Matchers.notNullValue())
                .body("errorCode", Matchers.matchesRegex("\\d{5}"));
    }
}
