package com.rest.restapp.service;

import com.rest.restapp.dto.request.NoteRequestTo;
import com.rest.restapp.dto.response.NoteResponseTo;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.NoteMapper;
import com.rest.restapp.repository.IssueRepository;
import com.rest.restapp.repository.NoteRepository;
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

    NoteRepository noteRepository;
    IssueRepository issueRepository;
    NoteMapper mapper;

    @Transactional
    public NoteResponseTo createNote(NoteRequestTo requestTo) {
        validateNoteRequest(requestTo);
        var issue = issueRepository
                .findById(requestTo.issueId())
                .orElseThrow(() ->
                        new NotFoundException("Issue with id " + requestTo.issueId() + " not found")
                );

        var note = mapper.toEntity(requestTo);
        note.setIssue(issue);
        var savedNotice = noteRepository.save(note);
        return mapper.toResponseTo(savedNotice);
    }

    public NoteResponseTo getNoteById(Long id) {
        var notice = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note with id " + id + " not found"));
        return mapper.toResponseTo(notice);
    }

    public List<NoteResponseTo> getAllNotices() {
        return noteRepository.findAll().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public NoteResponseTo updateNote(Long id, NoteRequestTo requestTo) {
        validateNoteRequest(requestTo);
        var existingNotice = noteRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Note with id " + id + " not found")
                );

        var issue = issueRepository
                .findById(requestTo.issueId())
                .orElseThrow(() ->
                        new NotFoundException("Issue with id " + requestTo.issueId() + " not found")
                );

        mapper
                .updateEntityFromDto(requestTo, existingNotice);
        existingNotice
                .setIssue(issue);
        return mapper
                .toResponseTo(
                        noteRepository.save(existingNotice)
                );
    }

    @Transactional
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new NotFoundException("Note with id " + id + " not found");
        }
        noteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseTo> getNotesByIssueId(Long issueId) {
        return noteRepository.findAll().stream()
                .filter(notice ->
                        notice
                                .getIssue()
                                .getId()
                                .equals(issueId))
                .map(mapper::toResponseTo)
                .toList();
    }

    private void validateNoteRequest(NoteRequestTo requestTo) {
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