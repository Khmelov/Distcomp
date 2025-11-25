package com.rest.restapp;

import com.rest.restapp.dto.request.AuthorRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/v1.0/authors";

    @Test
    void createAuthorTest_shouldReturnCreated() {
        var request = new AuthorRequestToDto("testlogin", "pass123", "John", "Doe");

        var response = restTemplate.postForEntity(BASE_URL, request, AuthorResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getAuthorByIdTest_shouldReturnOk() {
        var request = new AuthorRequestToDto("login2", "pass", "Jane", "Smith");
        var created = restTemplate.postForEntity(BASE_URL, request, AuthorResponseToDto.class);
        assertThat(created.getBody())
                .isNotNull();

        Long id = created.getBody().id();

        var response = restTemplate.getForEntity(BASE_URL + "/" + id, AuthorResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getAllAuthorsTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(BASE_URL, AuthorResponseToDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateAuthorTest_shouldReturnOk() {
        var request = new AuthorRequestToDto("updatelogin", "oldpass", "Old", "Name");
        var created = restTemplate.postForEntity(BASE_URL, request, AuthorResponseToDto.class);
        assertThat(created.getBody())
                .isNotNull();
        Long id = created.getBody().id();

        var updateRequest = new AuthorRequestToDto("newlogin", "newpass", "New", "Name");
        var putEntity = new HttpEntity<>(updateRequest);

        var response = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                putEntity,
                AuthorResponseToDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deleteAuthorTest_shouldReturnNoContent() {
        var request = new AuthorRequestToDto("todelete", "123", "Del", "Me");
        var created = restTemplate.postForEntity(BASE_URL, request, AuthorResponseToDto.class);
        assertThat(created.getBody())
                .isNotNull();
        Long id = created.getBody().id();

        var response = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}