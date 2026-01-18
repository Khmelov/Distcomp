package com.rest.restapp.service;

import com.rest.restapp.client.DiscussionClient;
import com.rest.restapp.dto.request.NoteRequestToDto;
import com.rest.restapp.dto.response.NoteResponseTo;
import com.rest.restapp.exception.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NoteService {

    DiscussionClient discussionClient;

    public NoteResponseTo createNote(NoteRequestToDto requestTo) {
        validateNoteRequest(requestTo);
        return discussionClient.createNote(requestTo);
    }

    public NoteResponseTo getNoteById(Long id) {
        return discussionClient.getById(id);
    }

    public List<NoteResponseTo> getAllNotes() {
        return discussionClient.getAll();
    }

    @Transactional
    public NoteResponseTo updateNote(Long id, NoteRequestToDto requestTo) {
        validateNoteRequest(requestTo);
        return discussionClient.update(id, requestTo);
    }

    @Transactional
    public void deleteNote(Long id) {
        discussionClient.deleteNote(id);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseTo> getNotesByIssueId(Long issueId) {
        return discussionClient.getNotesByIssueId(issueId);
    }

    private void validateNoteRequest(NoteRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Note request cannot be null");
        }
        if (requestTo.issueId() == null) {
            throw new ValidationException("Issue ID is required");
        }
        if (requestTo.content() == null || requestTo.content().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
    }
}
