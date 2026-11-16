package by.bsuir.discussion;

import by.bsuir.discussion.config.CassandraKeyspaceInitializer;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.CassandraContainer;

import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@EnabledIf("dockerAvailable")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscussionCassandraTest {

    static final CassandraContainer<?> CASSANDRA = startCassandra();

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void cassandraProperties(DynamicPropertyRegistry registry) {
        String host = CASSANDRA.getHost();
        int cassandraPort = CASSANDRA.getMappedPort(9042);
        String datacenter = CASSANDRA.getLocalDatacenter();
        registry.add("spring.cassandra.contact-points", () -> host);
        registry.add("spring.cassandra.port", () -> cassandraPort);
        registry.add("spring.cassandra.local-datacenter", () -> datacenter);
        registry.add("spring.cassandra.keyspace-name", () -> "distcomp");
        CassandraKeyspaceInitializer.ensureKeyspace(host, cassandraPort, datacenter, "distcomp");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Сообщение живёт в Cassandra, партиция — story_id, поиск по id идёт через вторую таблицу")
    void storesMessageByStoryAndById() {
        int id = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"storyId\":15,\"content\":\"cassandra-text\"}")
                .post("/messages")
                .then()
                .statusCode(201)
                .body("storyId", equalTo(15))
                .body("content", equalTo("cassandra-text"))
                .extract()
                .jsonPath()
                .getInt("id");

        RestAssured.given()
                .get("/messages/{id}", id)
                .then()
                .statusCode(200)
                .body("content", equalTo("cassandra-text"));

        RestAssured.given()
                .get("/messages?storyId=15")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(id));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"id\":" + id + ",\"storyId\":16,\"content\":\"moved\"}")
                .put("/messages/{id}", id)
                .then()
                .statusCode(200)
                .body("storyId", equalTo(16))
                .body("content", equalTo("moved"));

        try (CqlSession session = session()) {
            Row moved = session.execute("SELECT id, content FROM distcomp.tbl_message WHERE story_id = 16").one();
            assertThat(moved).isNotNull();
            assertThat(moved.getLong("id")).isEqualTo(id);
            assertThat(moved.getString("content")).isEqualTo("moved");
            assertThat(session.execute("SELECT id FROM distcomp.tbl_message WHERE story_id = 15").one()).isNull();

            Row byId = session.execute("SELECT story_id, content FROM distcomp.tbl_message_by_id WHERE id = " + id).one();
            assertThat(byId).isNotNull();
            assertThat(byId.getLong("story_id")).isEqualTo(16L);

            String storyKind = session.execute(
                            "SELECT kind FROM system_schema.columns WHERE keyspace_name = 'distcomp' "
                                    + "AND table_name = 'tbl_message' AND column_name = 'story_id'")
                    .one()
                    .getString("kind");
            assertThat(storyKind).isEqualTo("partition_key");
        }

        RestAssured.given()
                .delete("/messages/{id}", id)
                .then()
                .statusCode(204);
        RestAssured.given()
                .get("/messages/{id}", id)
                .then()
                .statusCode(404)
                .body("errorCode", equalTo(40401));
    }

    private CqlSession session() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(CASSANDRA.getHost(), CASSANDRA.getMappedPort(9042)))
                .withLocalDatacenter(CASSANDRA.getLocalDatacenter())
                .withKeyspace("distcomp")
                .build();
    }

    static boolean dockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable exception) {
            return false;
        }
    }

    private static CassandraContainer<?> startCassandra() {
        if (!dockerAvailable()) {
            return null;
        }
        CassandraContainer<?> container = new CassandraContainer<>("cassandra:4.1");
        container.start();
        return container;
    }
}
