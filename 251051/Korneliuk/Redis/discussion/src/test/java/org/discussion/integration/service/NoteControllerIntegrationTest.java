package org.discussion.integration.service;

import com.common.NoteResponseTo;
import org.discussion.integration.configuration.CassandraIntegrationTest;
import org.discussion.dto.request.NoteRequestTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class NoteControllerIntegrationTest extends CassandraIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String NOTES_URL = "/api/v1.0/notes";

    @Test
    void createNoteTest_shouldReturnCreated() {
        var issueId = 15L;
        var noteRequest = new NoteRequestTo(issueId, "This is a note");

        var response = restTemplate.postForEntity(NOTES_URL, noteRequest, NoteResponseTo.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getNoteByIdTest_shouldReturnOk() {
        // Подготовка
        var issueId = 15L;
        var note = restTemplate.postForEntity(NOTES_URL,
                new NoteRequestTo(issueId, "Note content"),
                NoteResponseTo.class);
        assertThat(note.getBody())
                .isNotNull();
        Long noteId = note.getBody().id();

        var response = restTemplate.getForEntity(NOTES_URL + "/" + noteId, NoteResponseTo.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void getAllNotesTest_shouldReturnOk() {
        var response = restTemplate.getForEntity(NOTES_URL, NoteResponseTo[].class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }


    @Test
    void updateNoteTest_shouldReturnOk() {
        // Подготовка
        var issueId = 15L;
        var note = restTemplate.postForEntity(NOTES_URL,
                new NoteRequestTo(issueId, "Old note"),
                NoteResponseTo.class);
        assertThat(note.getBody())
                .isNotNull();

        Long noteId = note.getBody().id();
        var updateRequest = new NoteRequestTo(issueId, "Updated note content");
        var putEntity = new HttpEntity<>(updateRequest);

        var response = restTemplate.exchange(
                NOTES_URL + "/" + noteId,
                HttpMethod.PUT,
                putEntity,
                NoteResponseTo.class
        );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
    }

    @Test
    void deleteNoteTest_shouldReturnNoContent() {
        // Подготовка
        var issueId = 15L;
        var note = restTemplate.postForEntity(NOTES_URL,
                new NoteRequestTo(issueId, "To delete"),
                NoteResponseTo.class);
        assertThat(note.getBody())
                .isNotNull();

        Long noteId = note.getBody().id();

        var response = restTemplate.exchange(
                NOTES_URL + "/" + noteId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getNotesByIssueIdTest_shouldReturnOk() {
        Long issueId = 15L;
        restTemplate.postForEntity(NOTES_URL,
                new NoteRequestTo(issueId, "Note 1"),
                NoteResponseTo.class);
        restTemplate.postForEntity(NOTES_URL,
                new NoteRequestTo(issueId, "Note 2"),
                NoteResponseTo.class);

        var response = restTemplate.getForEntity(NOTES_URL + "/issue/" + issueId, NoteResponseTo[].class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
        assertThat(response.getBody())
                .hasSizeGreaterThanOrEqualTo(2);
    }
}
