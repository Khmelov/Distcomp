package com.rest.restapp;

import com.rest.restapp.dto.request.UserRequestToDto;
import com.rest.restapp.dto.response.UserResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/v1.0/authors";

    @Test
    void createUserTest_shouldReturnCreated() {
        var request = new UserRequestToDto(
                "testlogin",
                "pass123",
                "John",
                "Doe"
        );
        var response = restTemplate.postForEntity(
                BASE_URL,
                request,
                UserResponseToDto.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getUserByIdTest_shouldReturnOk() {
        var request = new UserRequestToDto(
                "login2",
                "pass",
                "Jane",
                "Smith"
        );
        var created = restTemplate.postForEntity(
                BASE_URL,
                request,
                UserResponseToDto.class
        );
        assertThat(created.getBody())
                .isNotNull();

        Long id = created.getBody().id();
        var response = restTemplate.getForEntity(
                BASE_URL + "/" + id,
                UserResponseToDto.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getAllUsersTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(
                BASE_URL,
                UserResponseToDto[].class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void updateUserTest_shouldReturnOk() {
        var request = new UserRequestToDto(
                "updatelogin",
                "oldpass",
                "Old",
                "Name"
        );
        var created = restTemplate.postForEntity(
                BASE_URL,
                request,
                UserResponseToDto.class
        );
        assertThat(created.getBody())
                .isNotNull();

        Long id = created.getBody().id();
        var updateRequest = new UserRequestToDto(
                "newlogin",
                "newpass",
                "New",
                "Name");
        var response = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                UserResponseToDto.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void deleteUserTest_shouldReturnNoContent() {
        var request = new UserRequestToDto(
                "todelete",
                "123",
                "Del",
                "Me");
        var created = restTemplate.postForEntity(
                BASE_URL,
                request,
                UserResponseToDto.class
        );
        assertThat(created.getBody())
                .isNotNull();

        Long id = created.getBody().id();
        var response = restTemplate.exchange(
                BASE_URL + "/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
    }
}