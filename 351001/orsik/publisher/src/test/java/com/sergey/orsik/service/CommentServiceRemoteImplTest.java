package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.service.impl.CommentServiceRemoteImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentServiceRemoteImplTest {

    private MockWebServer server;
    private CommentServiceRemoteImpl service;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        RestClient client = RestClient.builder().baseUrl(server.url("/").toString()).build();
        service = new CommentServiceRemoteImpl(client);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void findByIdMaps404ToEntityNotFound() {
        server.enqueue(new MockResponse().setResponseCode(404).setHeader("Content-Type", "application/json")
                .setBody("{\"errorMessage\":\"Comment with id 1 not found\"}"));

        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createParsesJsonBody() {
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":55,\"tweetId\":2,\"content\":\"ok\",\"created\":\"2020-01-01T00:00:00Z\"}"));

        CommentResponseTo created = service.create(new CommentRequestTo(null, 2L, 1L, "ok", null));

        assertThat(created.getId()).isEqualTo(55L);
        assertThat(created.getTweetId()).isEqualTo(2L);
    }

    @Test
    void findAllParsesArray() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"id\":1,\"tweetId\":9,\"content\":\"a\",\"created\":null}]"));

        var list = service.findAll(0, 10, "id", "asc", 9L, null);
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getTweetId()).isEqualTo(9L);
    }
}
