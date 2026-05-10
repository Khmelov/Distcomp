package by.liza.discussion.service;

import by.liza.discussion.dto.request.NoteRequestTo;
import by.liza.discussion.dto.response.NoteResponseTo;
import by.liza.discussion.exception.EntityNotFoundException;
import by.liza.discussion.mapper.NoteMapper;
import by.liza.discussion.model.Note;
import by.liza.discussion.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final WebClient publisherWebClient;

    public NoteResponseTo create(NoteRequestTo requestTo) {
        // Проверяем что Article существует в publisher
        validateArticleExists(requestTo.getArticleId());

        Note note = noteMapper.toEntity(requestTo);
        if (note.getId() == null) {
            note.setId(System.currentTimeMillis());
        }
        Note saved = noteRepository.save(note);
        return noteMapper.toResponse(saved);
    }

    public NoteResponseTo getById(Long id) {
        return noteMapper.toResponse(findById(id));
    }

    public List<NoteResponseTo> getAll() {
        return noteMapper.toResponseList(noteRepository.findAll());
    }

    public NoteResponseTo update(NoteRequestTo requestTo) {
        if (requestTo.getId() == null) {
            throw new EntityNotFoundException("Note id must be provided for update", 40004);
        }
        // Проверяем что Article существует
        validateArticleExists(requestTo.getArticleId());

        Note existing = findById(requestTo.getId());
        existing.setContent(requestTo.getContent());
        existing.setArticleId(requestTo.getArticleId());
        Note updated = noteRepository.save(existing);
        return noteMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Note note = findById(id);
        noteRepository.delete(note);
    }

    public List<NoteResponseTo> getByArticleId(Long articleId) {
        return noteMapper.toResponseList(noteRepository.findByArticleId(articleId));
    }

    private void validateArticleExists(Long articleId) {
        try {
            publisherWebClient.get()
                    .uri("/api/v1.0/articles/{id}", articleId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new EntityNotFoundException(
                    "Article with id " + articleId + " not found", 40402);
        } catch (Exception ex) {
            throw new EntityNotFoundException(
                    "Article with id " + articleId + " not found", 40402);
        }
    }

    private Note findById(Long id) {
        return noteRepository.findAll().stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Note with id " + id + " not found", 40404));
    }
}