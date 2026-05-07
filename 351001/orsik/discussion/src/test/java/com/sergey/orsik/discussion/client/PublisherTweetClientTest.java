package com.sergey.orsik.discussion.client;

import com.sergey.orsik.discussion.exception.EntityNotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PublisherTweetClientTest {

    private MockWebServer server;
    private PublisherTweetClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        RestClient rc = RestClient.builder().baseUrl(server.url("/").toString()).build();
        client = new PublisherTweetClient(rc);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void requireTweetExistsWhenOk() {
        server.enqueue(new MockResponse().setResponseCode(200));
        assertThatCode(() -> client.requireTweetExists(1L)).doesNotThrowAnyException();
    }

    @Test
    void requireTweetExistsWhen404() {
        server.enqueue(new MockResponse().setResponseCode(404));
        assertThatThrownBy(() -> client.requireTweetExists(9L)).isInstanceOf(EntityNotFoundException.class);
    }
}
