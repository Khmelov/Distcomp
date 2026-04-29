package com.sergey.orsik.discussion;

import com.sergey.orsik.discussion.client.PublisherTweetClient;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class DiscussionApplicationIT {

    @Container
    @SuppressWarnings("resource")
    static final CassandraContainer CASSANDRA = new CassandraContainer("cassandra:5");

    @DynamicPropertySource
    static void registerCassandra(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", CASSANDRA::getHost);
        registry.add("spring.cassandra.port", () -> CASSANDRA.getFirstMappedPort());
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "distcomp");
        registry.add("spring.liquibase.url", () -> String.format(
                "jdbc:cassandra://%s:%d/distcomp?localdatacenter=datacenter1",
                CASSANDRA.getHost(),
                CASSANDRA.getFirstMappedPort()));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private PublisherTweetClient publisherTweetClient;

    @Test
    void commentLifecycleAgainstCassandra() {
        doNothing().when(publisherTweetClient).requireTweetExists(anyLong());

        CommentRequestTo createReq = new CommentRequestTo(null, 1001L, "integration comment", null);
        ResponseEntity<CommentResponseTo> created = restTemplate.postForEntity(
                "/api/v1.0/comments",
                new HttpEntity<>(createReq),
                CommentResponseTo.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().getId()).isNotNull();
        assertThat(created.getBody().getTweetId()).isEqualTo(1001L);

        Long id = created.getBody().getId();

        ResponseEntity<CommentResponseTo> byId = restTemplate.getForEntity(
                "/api/v1.0/comments/" + id,
                CommentResponseTo.class);
        assertThat(byId.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byId.getBody().getContent()).isEqualTo("integration comment");

        CommentResponseTo[] list = restTemplate.getForObject(
                "/api/v1.0/comments?tweetId=1001&page=0&size=10",
                CommentResponseTo[].class);
        assertThat(list).isNotEmpty();

        restTemplate.delete("/api/v1.0/comments/by-tweet/1001");

        ResponseEntity<CommentResponseTo> after = restTemplate.getForEntity(
                "/api/v1.0/comments/" + id,
                CommentResponseTo.class);
        assertThat(after.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
