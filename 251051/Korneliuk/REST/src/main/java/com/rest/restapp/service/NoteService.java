package com.rest.restapp.service;

import com.rest.restapp.dto.request.NoteRequestToDto;
import com.rest.restapp.dto.response.NoteResponseTo;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.NoteMapper;
import com.rest.restapp.repository.InMemoryRepository;
import jakarta.validation.ValidationException;
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

    InMemoryRepository repository;
    NoteMapper mapper;

    @Transactional
    public NoteResponseTo createNote(NoteRequestToDto requestTo) {
        validateNoteRequest(requestTo);
        var issue = repository.findIssueById(requestTo.issueId())
                .orElseThrow(() -> new NotFoundException("Issue with id " + requestTo.issueId() + " not found"));

        var notice = mapper.toEntity(requestTo);
        notice.setIssue(issue);
        var savedNote = repository.saveNote(notice);
        return mapper.toResponseTo(savedNote);
    }

    public NoteResponseTo getNoteById(Long id) {
        var note = repository.findNoteById(id)
                .orElseThrow(() -> new NotFoundException("Note with id " + id + " not found"));
        return mapper.toResponseTo(note);
    }

    public List<NoteResponseTo> getAllNotes() {
        return repository.findAllNotes().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public NoteResponseTo updateNote(Long id, NoteRequestToDto requestTo) {
        validateNoteRequest(requestTo);
        var existingNote = repository.findNoteById(id)
                .orElseThrow(() -> new NotFoundException("Note with id " + id + " not found"));

        var issue = repository.findIssueById(requestTo.issueId())
                .orElseThrow(() -> new NotFoundException("Issue with id " + requestTo.issueId() + " not found"));

        mapper.updateEntityFromDto(requestTo, existingNote);
        existingNote.setIssue(issue);
        var updatedNote = repository.saveNote(existingNote);
        return mapper.toResponseTo(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        if (!repository.existsNoteById(id)) {
            throw new NotFoundException("Notice with id " + id + " not found");
        }
        repository.deleteNoteById(id);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseTo> getNotesByIssueId(Long issueId) {
        return repository.findAllNotes().stream()
                .filter(note -> note.getIssue().getId().equals(issueId))
                .map(mapper::toResponseTo)
                .toList();
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