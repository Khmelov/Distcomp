package by.bsuir.distcomp.discussion;

import by.bsuir.distcomp.discussion.config.CassandraKeyspaceInitializer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = CassandraKeyspaceInitializer.class)
class DiscussionCassandraIT {

    @Container
    static CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"));

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", cassandra::getHost);
        registry.add("spring.cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "distcomp");
        registry.add("spring.liquibase.url", () -> String.format(
                "jdbc:cassandra://%s:%d/?compliancemode=Liquibase&localdatacenter=datacenter1",
                cassandra.getHost(), cassandra.getMappedPort(9042)));
        registry.add("spring.liquibase.driver-class-name", () -> "com.ing.data.cassandra.jdbc.CassandraDriver");
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void createAndGetReaction() {
        String body = """
                {"tweetId": 100, "content": "integration content"}
                """;

        int id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/reactions")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("tweetId", equalTo(100))
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/v1.0/reactions/" + id)
                .then()
                .statusCode(200)
                .body("content", equalTo("integration content"));
    }

    @Test
    void listByTweet() {
        String create = """
                {"tweetId": 200, "content": "list me"}
                """;
        given().contentType(ContentType.JSON).body(create).post("/api/v1.0/reactions").then().statusCode(201);

        given()
                .when()
                .get("/api/v1.0/reactions/tweet/200")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }
}
