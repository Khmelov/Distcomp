package org.example.newsapi.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.newsapi.AbstractIntegrationTest;
import org.example.newsapi.dto.request.NewsRequestTo;
import org.example.newsapi.entity.News;
import org.example.newsapi.entity.User;
import org.example.newsapi.repository.NewsRepository;
import org.example.newsapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class NewsControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1.0/news";
    }

    @AfterEach
    void tearDown() {
        // Очищаем новости после каждого теста, чтобы не влиять на другие тесты
        newsRepository.deleteAll();
    }

    @Test
    void shouldCreateNews() {
        // ID=1 создается автоматически скриптом Liquibase (sashabrazhalovich2005@gmail.com)
        Long userId = 1L;

        NewsRequestTo request = new NewsRequestTo();
        request.setUserId(userId);
        request.setTitle("Breaking News");
        request.setContent("Something happened today.");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Breaking News"))
                .body("userId", equalTo(userId.intValue()));
    }

    @Test
    void shouldGetAllNewsWithPagination() {
        // Подготовка данных напрямую через репозиторий
        User user = userRepository.findById(1L).orElseThrow();

        News news1 = News.builder().user(user).title("Title 1").content("Content 1").build();
        News news2 = News.builder().user(user).title("Title 2").content("Content 2").build();
        newsRepository.saveAll(List.of(news1, news2));

        // Проверка GET запроса с пагинацией
        given()
                .param("page", 0)
                .param("size", 10)
                .param("sort", "id,asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("content[0].title", equalTo("Title 1"))
                .body("totalElements", equalTo(2));
    }

    @Test
    void shouldUpdateNews() {
        // Создаем новость
        User user = userRepository.findById(1L).orElseThrow();
        News news = News.builder().user(user).title("Old Title").content("Old Content").build();
        news = newsRepository.save(news);

        // Формируем запрос на обновление
        NewsRequestTo updateRequest = new NewsRequestTo();
        updateRequest.setUserId(user.getId()); // Автор остается тот же
        updateRequest.setTitle("New Title");
        updateRequest.setContent("New Content");

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/{id}", news.getId())
                .then()
                .statusCode(200)
                .body("title", equalTo("New Title"))
                .body("content", equalTo("New Content"));
    }

    @Test
    void shouldDeleteNews() {
        // Создаем новость
        User user = userRepository.findById(1L).orElseThrow();
        News news = News.builder().user(user).title("To Delete").content("...").build();
        news = newsRepository.save(news);

        // Удаляем
        given()
                .when()
                .delete("/{id}", news.getId())
                .then()
                .statusCode(204);

        // Проверяем, что удалилась (ожидаем 404 при попытке получить)
        given()
                .when()
                .get("/{id}", news.getId())
                .then()
                .statusCode(404);
    }
}