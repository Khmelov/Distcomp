package com.rest.restapp;

import com.rest.restapp.dto.request.TagRequestToDto;
import com.rest.restapp.dto.response.TagResponseToDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TagControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TAGS_URL = "/api/v1.0/tags";

    @Test
    void createTagTest_shouldReturnCreated() {
        var request = new TagRequestToDto("bug");

        var response = restTemplate.postForEntity(TAGS_URL, request, TagResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getTagByIdTest_shouldReturnOk() {
        var created = restTemplate.postForEntity(TAGS_URL, new TagRequestToDto("feature"), TagResponseToDto.class);
        assertThat(created.getBody())
                .isNotNull();
        Long id = created.getBody().id();

        var response = restTemplate.getForEntity(TAGS_URL + "/" + id, TagResponseToDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getAllTagsTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(TAGS_URL, TagResponseToDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateTagTest_shouldReturnOk() {
        var created = restTemplate.postForEntity(TAGS_URL, new TagRequestToDto("old"), TagResponseToDto.class);
        assertThat(created.getBody())
                .isNotNull();
        Long id = created.getBody().id();

        var updateRequest = new TagRequestToDto("updated");
        var putEntity = new HttpEntity<>(updateRequest);

        var response = restTemplate.exchange(
                TAGS_URL + "/" + id,
                HttpMethod.PUT,
                putEntity,
                TagResponseToDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deleteTagTest_shouldReturnNoContent() {
        var created = restTemplate.postForEntity(TAGS_URL, new TagRequestToDto("todelete"), TagResponseToDto.class);
        assertThat(created.getBody())
                .isNotNull();
        Long id = created.getBody().id();

        var response = restTemplate.exchange(
                TAGS_URL + "/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}