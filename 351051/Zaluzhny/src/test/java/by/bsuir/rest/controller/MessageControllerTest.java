package by.bsuir.rest.controller;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.support.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

class MessageControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("Сообщение создаётся только для существующей истории и проходит CRUD")
    void crudMessage() {
        int userId = createUser();
        int storyId = createStory(userId);
        int anotherStoryId = createStory(userId);
        String content = "ok";

        int id = createMessage(storyId, content);

        when().get("/messages/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("storyId", equalTo(storyId))
                .body("content", equalTo(content));

        when().get("/messages")
                .then()
                .statusCode(200)
                .body("id", hasItem(id));

        MessageRequestTo update = new MessageRequestTo();
        update.setStoryId((long) anotherStoryId);
        update.setContent("updated-message");
        givenJson(update)
                .put("/messages/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("storyId", equalTo(anotherStoryId))
                .body("content", equalTo("updated-message"));

        when().delete("/messages/{id}", id)
                .then()
                .statusCode(204);
        assertError(when().get("/messages/{id}", id).then(), 404, 40401);
    }

    @Test
    @DisplayName("Пустая история и короткие тексты сообщения отклоняются")
    void rejectsInvalidMessage() {
        MessageRequestTo missingStory = new MessageRequestTo();
        missingStory.setStoryId(9_000_003L);
        missingStory.setContent("ok");
        assertError(givenJson(missingStory).post("/messages").then(), 400, 40002);

        int storyId = createStory(createUser());
        MessageRequestTo tooShort = new MessageRequestTo();
        tooShort.setStoryId((long) storyId);
        tooShort.setContent("a");
        assertError(givenJson(tooShort).post("/messages").then(), 400, 40001);

        MessageRequestTo tooLong = new MessageRequestTo();
        tooLong.setStoryId((long) storyId);
        tooLong.setContent("m".repeat(2049));
        assertError(givenJson(tooLong).post("/messages").then(), 400, 40001);

        String maxContent = "m".repeat(2048);
        int id = createMessage(storyId, maxContent);
        when().get("/messages/{id}", id)
                .then()
                .statusCode(200)
                .body("content", equalTo(maxContent));
    }

    @Test
    @DisplayName("Чтение и удаление отсутствующего сообщения возвращают 404")
    void missingMessage() {
        assertError(when().get("/messages/{id}", 9_000_004).then(), 404, 40401);
        assertError(when().delete("/messages/{id}", 9_000_004).then(), 404, 40401);
    }
}
