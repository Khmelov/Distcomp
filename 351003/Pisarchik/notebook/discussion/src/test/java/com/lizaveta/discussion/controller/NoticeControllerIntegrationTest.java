package com.lizaveta.discussion.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class NoticeControllerIntegrationTest {

    @Container
    public static final CassandraContainer<?> CASSANDRA = new CassandraContainer<>(
            DockerImageName.parse("cassandra:5.0"));

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void registerCassandra(final DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", CASSANDRA::getHost);
        registry.add("spring.cassandra.port", () -> String.valueOf(CASSANDRA.getFirstMappedPort()));
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "distcomp");
        registry.add("spring.liquibase.url", () -> "jdbc:cassandra://" + CASSANDRA.getHost() + ":"
                + CASSANDRA.getFirstMappedPort() + "/system?localdatacenter=datacenter1&compliancemode=Liquibase");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void createAndGetNotice_OnPort24130Contract() {
        String body = """
                {"notice":{"storyId":100,"content":"Integration comment"}}
                """;
        int id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/v1.0/notices")
                .then()
                .statusCode(201)
                .body("notice.id", notNullValue())
                .body("notice.storyId", equalTo(100))
                .body("notice.content", equalTo("Integration comment"))
                .extract()
                .path("notice.id");
        given()
                .when()
                .get("/api/v1.0/notices/" + id)
                .then()
                .statusCode(200)
                .body("notice.content", equalTo("Integration comment"));
    }

    @Test
    void findByStoryEndpoint_ShouldReturnList() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"notice\":{\"storyId\":200,\"content\":\"s1\"}}")
                .post("/api/v1.0/notices");
        given()
                .contentType(ContentType.JSON)
                .body("{\"notice\":{\"storyId\":200,\"content\":\"s2\"}}")
                .post("/api/v1.0/notices");
        given()
                .when()
                .get("/api/v1.0/notices/by-story/200")
                .then()
                .statusCode(200);
    }
}
