package by.bsuir.rest.controller;

import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.request.StoryRequestTo;
import by.bsuir.rest.support.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

class StoryControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("История создаётся без id и меток времени, метки времени совпадают")
    void createsStoryWithServerTimestamps() {
        int userId = createUser();
        String storyTitle = title();
        String content = storyContent();
        StoryRequestTo request = story(userId, storyTitle, content);
        request.setId(999_999_999L);

        var response = givenJson(request)
                .post("/stories")
                .then()
                .statusCode(201)
                .body("id", not(equalTo(999_999_999)))
                .body("userId", equalTo(userId))
                .body("title", equalTo(storyTitle))
                .body("content", equalTo(content))
                .body("created", startsWith("20"))
                .body("modified", startsWith("20"))
                .extract();

        int id = response.jsonPath().getInt("id");
        String created = response.path("created");
        String modified = response.path("modified");
        org.junit.jupiter.api.Assertions.assertEquals(created, modified);

        when().get("/stories/{id}", id)
                .then()
                .statusCode(200)
                .body("created", equalTo(created));

        when().get("/stories")
                .then()
                .statusCode(200)
                .body("id", hasItem(id));
    }

    @Test
    @DisplayName("Обновление меняет поля и modified, created остаётся прежним")
    void updatesStory() {
        int userId = createUser();
        int anotherUserId = createUser();
        int id = createStory(userId);
        String created = when().get("/stories/{id}", id).then().extract().path("created");

        StoryRequestTo update = story(anotherUserId, title(), storyContent());
        givenJson(update)
                .put("/stories")
                .then()
                .statusCode(400);

        update.setId((long) id);
        givenJson(update)
                .put("/stories/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("userId", equalTo(anotherUserId))
                .body("title", equalTo(update.getTitle()))
                .body("content", equalTo(update.getContent()))
                .body("created", equalTo(created))
                .body("modified", not(equalTo(created)));

        when().get("/stories/{id}/user", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(anotherUserId));
    }

    @Test
    @DisplayName("Заголовок уникален, длины title и content проверяются, чужой userId отклоняется")
    void validatesStoryConstraints() {
        StoryRequestTo unknownUser = story(9_000_010, title(), storyContent());
        assertError(givenJson(unknownUser).post("/stories").then(), 400, 40002);

        int userId = createUser();
        StoryRequestTo shortTitle = story(userId, "a", storyContent());
        assertError(givenJson(shortTitle).post("/stories").then(), 400, 40001);

        StoryRequestTo longTitle = story(userId, "t".repeat(65), storyContent());
        assertError(givenJson(longTitle).post("/stories").then(), 400, 40001);

        StoryRequestTo shortContent = story(userId, title(), "abc");
        assertError(givenJson(shortContent).post("/stories").then(), 400, 40001);

        StoryRequestTo longContent = story(userId, title(), "c".repeat(2049));
        assertError(givenJson(longContent).post("/stories").then(), 400, 40001);

        String uniqueTitle = title();
        createStory(story(userId, uniqueTitle, "abcd"));
        StoryRequestTo duplicate = story(userId, uniqueTitle, storyContent());
        assertError(givenJson(duplicate).post("/stories").then(), 403, 40301);

        int existingId = createStory(userId);
        StoryRequestTo taken = story(userId, uniqueTitle, storyContent());
        taken.setId((long) existingId);
        assertError(givenJson(taken).put("/stories/{id}", existingId).then(), 403, 40301);

        StoryRequestTo sameTitle = story(userId, uniqueTitle, "kept");
        sameTitle.setId((long) when().get("/stories").then().extract().jsonPath()
                .getInt("find { it.title == '" + uniqueTitle + "' }.id"));
        givenJson(sameTitle)
                .put("/stories/{id}", sameTitle.getId())
                .then()
                .statusCode(200)
                .body("title", equalTo(uniqueTitle))
                .body("content", equalTo("kept"));

        givenJson(story(userId, "t".repeat(64), "c".repeat(2048)))
                .post("/stories")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Маркеры и сообщения истории читаются по id истории")
    void readsRelatedMarkersAndMessages() {
        int userId = createUser();
        int firstMarker = createMarker();
        int secondMarker = createMarker();
        StoryRequestTo request = story(userId, title(), storyContent());
        request.setMarkerIds(List.of((long) secondMarker, (long) firstMarker, (long) firstMarker));
        int storyId = createStory(request);

        when().get("/stories/{id}", storyId)
                .then()
                .statusCode(200)
                .body("markerIds", equalTo(List.of(firstMarker, secondMarker)));

        when().get("/stories/{id}/markers", storyId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("id", hasItem(firstMarker))
                .body("id", hasItem(secondMarker));

        int messageId = createMessage(storyId, "hello");
        createMessage(storyId, "world");
        when().get("/stories/{id}/messages", storyId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("id", hasItem(messageId));

        when().get("/stories/{id}/user", storyId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId));

        StoryRequestTo clearMarkers = story(userId, title(), storyContent());
        clearMarkers.setId((long) storyId);
        clearMarkers.setMarkerIds(List.of());
        givenJson(clearMarkers)
                .put("/stories/{id}", storyId)
                .then()
                .statusCode(200)
                .body("markerIds", nullValue());

        when().get("/stories/{id}/markers", storyId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @DisplayName("Маркеры по имени внутри истории создаются, сохраняются и привязываются")
    void createsMarkersEmbeddedInStory() {
        int userId = createUser();
        String red = "red" + markerName().substring(0, 6);
        String green = "green" + markerName().substring(0, 6);
        String blue = "blue" + markerName().substring(0, 6);
        String body = """
                {
                  "userId": %d,
                  "title": "%s",
                  "content": "%s",
                  "markers": [
                    {"name": "%s"},
                    "%s",
                    {"name": "%s"}
                  ]
                }
                """.formatted(userId, title(), storyContent(), red, green, blue);

        int storyId = given()
                .contentType(io.restassured.http.ContentType.JSON)
                .body(body)
                .post("/stories")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");

        when().get("/stories/{id}/markers", storyId)
                .then()
                .statusCode(200)
                .body("name", hasItems(red, green, blue));

        when().get("/markers")
                .then()
                .statusCode(200)
                .body("name", hasItems(red, green, blue));

        int anotherStoryId = createStory(story(userId, title(), storyContent()));
        StoryRequestTo attachExisting = story(userId, title(), storyContent());
        attachExisting.setId((long) anotherStoryId);
        MarkerRequestTo existing = new MarkerRequestTo();
        existing.setName(red);
        attachExisting.setMarkers(List.of(existing));
        givenJson(attachExisting)
                .put("/stories/{id}", anotherStoryId)
                .then()
                .statusCode(200);

        when().get("/stories/{id}/markers", anotherStoryId)
                .then()
                .statusCode(200)
                .body("name", equalTo(List.of(red)));
    }

    @Test
    @DisplayName("Удаление истории удаляет её маркеры, общий маркер остаётся")
    void deletesMarkersTogetherWithStory() {
        int userId = createUser();
        String own = "red" + markerName().substring(0, 6);
        String shared = "green" + markerName().substring(0, 6);
        int sharedId = createMarker(shared);

        StoryRequestTo first = story(userId, title(), storyContent());
        MarkerRequestTo ownMarker = new MarkerRequestTo();
        ownMarker.setName(own);
        MarkerRequestTo sharedMarker = new MarkerRequestTo();
        sharedMarker.setId((long) sharedId);
        first.setMarkers(List.of(ownMarker, sharedMarker));
        int firstId = createStory(first);

        StoryRequestTo second = story(userId, title(), storyContent());
        second.setMarkerIds(List.of((long) sharedId));
        int secondId = createStory(second);

        when().delete("/stories/{id}", firstId).then().statusCode(204);

        when().get("/markers")
                .then()
                .statusCode(200)
                .body("name", not(hasItem(own)))
                .body("name", hasItem(shared));
        when().get("/stories/{id}/markers", secondId)
                .then()
                .statusCode(200)
                .body("name", hasItem(shared));

        when().delete("/stories/{id}", secondId).then().statusCode(204);
        when().get("/markers")
                .then()
                .statusCode(200)
                .body("name", not(hasItem(shared)));
    }

    @Test
    @DisplayName("Связь с маркером сохраняется, если список маркеров не передан, и снимается при удалении маркера")
    void keepsMarkersUntilMarkerDeleted() {
        int userId = createUser();
        int markerId = createMarker();
        StoryRequestTo request = story(userId, title(), storyContent());
        request.setMarkerIds(List.of((long) markerId));
        int storyId = createStory(request);

        StoryRequestTo update = story(userId, title(), storyContent());
        update.setId((long) storyId);
        givenJson(update)
                .put("/stories/{id}", storyId)
                .then()
                .statusCode(200)
                .body("markerIds", equalTo(List.of(markerId)));

        when().delete("/markers/{id}", markerId).then().statusCode(204);

        when().get("/stories/{id}/markers", storyId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @DisplayName("Поиск историй сочетает login, title, content, markerId и markerName")
    void searchesStoriesByOptionalCriteria() {
        String authorLogin = login();
        int userId = createUser(authorLogin);
        int otherUserId = createUser();
        int alpha = createMarker();
        int beta = createMarker();
        String alphaName = when().get("/markers/{id}", alpha).then().extract().path("name");
        String betaName = when().get("/markers/{id}", beta).then().extract().path("name");

        String matchedTitle = title();
        String matchedContent = storyContent();
        StoryRequestTo matched = story(userId, matchedTitle, matchedContent);
        matched.setMarkerIds(List.of((long) alpha, (long) beta));
        int matchedId = createStory(matched);

        StoryRequestTo onlyAlpha = story(userId, title(), storyContent());
        onlyAlpha.setMarkerIds(List.of((long) alpha));
        int partialId = createStory(onlyAlpha);

        StoryRequestTo otherAuthor = story(otherUserId, title(), matchedContent);
        int otherId = createStory(otherAuthor);

        given()
                .queryParam("login", authorLogin)
                .queryParam("title", matchedTitle)
                .queryParam("content", matchedContent)
                .queryParam("markerId", alpha)
                .queryParam("markerId", beta)
                .queryParam("markerName", alphaName)
                .queryParam("markerName", betaName)
                .when()
                .get("/stories")
                .then()
                .statusCode(200)
                .body("id", hasItem(matchedId))
                .body("id", not(hasItem(partialId)))
                .body("id", not(hasItem(otherId)));

        given()
                .queryParam("title", "missing-" + matchedTitle)
                .when()
                .get("/stories")
                .then()
                .statusCode(200)
                .body("id", not(hasItem(matchedId)));

        assertError(when().get("/stories/{id}/user", 9_000_011).then(), 404, 40401);
        assertError(when().get("/stories/{id}/markers", 9_000_011).then(), 404, 40401);
        assertError(when().get("/stories/{id}/messages", 9_000_011).then(), 404, 40401);
        assertError(when().get("/stories/{id}", 9_000_011).then(), 404, 40401);
        assertError(when().delete("/stories/{id}", 9_000_011).then(), 404, 40401);
    }

    @Test
    @DisplayName("Несуществующий маркер в истории отклоняется, удаление автора не удаляет историю")
    void rejectsUnknownMarkerAndKeepsStoryAfterUserDelete() {
        int userId = createUser();
        StoryRequestTo request = story(userId, title(), storyContent());
        request.setMarkerIds(List.of(9_000_012L));
        assertError(givenJson(request).post("/stories").then(), 400, 40002);

        int storyId = createStory(userId);
        when().delete("/users/{id}", userId).then().statusCode(204);

        when().get("/stories/{id}", storyId)
                .then()
                .statusCode(200)
                .body("userId", equalTo(userId));
        assertError(when().get("/stories/{id}/user", storyId).then(), 404, 40401);
    }
}
