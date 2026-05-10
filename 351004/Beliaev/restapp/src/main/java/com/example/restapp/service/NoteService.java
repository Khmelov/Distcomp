package com.example.discussion.service;

import com.example.discussion.dto.request.NoteRequestTo;
import com.example.discussion.dto.response.NoteResponseTo;
import com.example.discussion.exception.EntityNotFoundException;
import com.example.discussion.mapper.NoteMapper;
import com.example.discussion.model.Article;
import com.example.discussion.model.Note;
import com.example.discussion.repository.ArticleRepository;
import com.example.discussion.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final ArticleRepository articleRepository;
    private final NoteMapper mapper;

    @Transactional
    public NoteResponseTo create(NoteRequestTo request) {
        Note note = mapper.toEntity(request);

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + request.getArticleId()));
        note.setArticle(article);

        Note saved = noteRepository.save(note);
        return mapper.toResponse(saved);
    }

    public List<NoteResponseTo> getAll() {
        return noteRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NoteResponseTo getById(Long id) {
        return noteRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));
    }

    @Transactional
    public NoteResponseTo update(Long id, NoteRequestTo request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));

        mapper.updateEntityFromDto(request, note);

        if (!note.getArticle().getId().equals(request.getArticleId())) {
            Article article = articleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + request.getArticleId()));
            note.setArticle(article);
        }

        Note saved = noteRepository.save(note);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new EntityNotFoundException("Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }
}