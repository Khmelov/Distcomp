package by.bsuir.rest.support;

import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.request.StoryRequestTo;
import by.bsuir.rest.dto.request.UserRequestTo;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTestSupport extends PostgresTestSupport {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1.0";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected UserRequestTo user(String login) {
        UserRequestTo request = new UserRequestTo();
        request.setLogin(login);
        request.setPassword("password123");
        request.setFirstname("Ivan");
        request.setLastname("Petrov");
        return request;
    }

    protected String login() {
        return "u-" + UUID.randomUUID() + "@test.dev";
    }

    protected String title() {
        return "t-" + UUID.randomUUID();
    }

    protected String storyContent() {
        return "content-" + UUID.randomUUID();
    }

    protected String markerName() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected int createUser() {
        return createUser(login());
    }

    protected int createUser(String login) {
        return givenJson(user(login))
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    protected StoryRequestTo story(int userId, String storyTitle, String content) {
        StoryRequestTo request = new StoryRequestTo();
        request.setUserId((long) userId);
        request.setTitle(storyTitle);
        request.setContent(content);
        return request;
    }

    protected int createStory(int userId) {
        return createStory(story(userId, title(), storyContent()));
    }

    protected int createStory(StoryRequestTo request) {
        return givenJson(request)
                .post("/stories")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    protected int createMarker() {
        return createMarker(markerName());
    }

    protected int createMarker(String name) {
        MarkerRequestTo request = new MarkerRequestTo();
        request.setName(name);
        return givenJson(request)
                .post("/markers")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    protected int createMessage(int storyId, String content) {
        MessageRequestTo request = new MessageRequestTo();
        request.setStoryId((long) storyId);
        request.setContent(content);
        return givenJson(request)
                .post("/messages")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    protected io.restassured.specification.RequestSpecification givenJson(Object body) {
        return io.restassured.RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body);
    }

    protected void assertError(ValidatableResponse response, int status, int errorCode) {
        response.statusCode(status)
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(errorCode))
                .body("errorMessage", not(equalTo("")));
    }
}
