package by.bsuir.rest.client;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

class RestDiscussionClientTest {

    private MockRestServiceServer server;
    private RestDiscussionClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://discussion");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new RestDiscussionClient(builder.build(), new ObjectMapper());
    }

    @Test
    @DisplayName("Создание уходит на discussion без префикса /api/v1.0")
    void createsMessage() {
        server.expect(requestTo("http://discussion/messages"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":5,\"storyId\":2,\"content\":\"ok\"}"));

        MessageRequestTo request = new MessageRequestTo();
        request.setStoryId(2L);
        request.setContent("ok");

        assertThat(client.create(request).getId()).isEqualTo(5L);
        server.verify();
    }

    @Test
    @DisplayName("404 discussion превращается в 40401")
    void mapsNotFound() {
        server.expect(requestTo("http://discussion/messages/9"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"errorMessage\":\"Message with id 9 not found\",\"errorCode\":40401}"));

        assertThatThrownBy(() -> client.getById(9L))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(40401);
    }

    @Test
    @DisplayName("Список по истории запрашивается параметром storyId")
    void findsByStory() {
        server.expect(requestTo("http://discussion/messages?storyId=4"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\":1,\"storyId\":4,\"content\":\"one\"}]"));

        assertThat(client.findByStoryId(4L)).hasSize(1);
        server.verify();
    }

    @Test
    @DisplayName("Удаление ждёт 204")
    void deletesMessage() {
        server.expect(requestTo("http://discussion/messages/3"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());

        client.delete(3L);
        server.verify();
    }
}
