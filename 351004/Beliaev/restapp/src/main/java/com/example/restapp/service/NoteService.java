package com.example.restapp.service;

import com.example.restapp.dto.request.NoteRequestTo;
import com.example.restapp.dto.response.NoteResponseTo;
import com.example.restapp.exception.EntityNotFoundException;
import com.example.restapp.mapper.NoteMapper;
import com.example.restapp.model.Note;
import com.example.restapp.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository repository;
    private final NoteMapper mapper;

    public NoteResponseTo create(NoteRequestTo request) {
        Note note = mapper.toEntity(request);
        Note saved = repository.save(note);
        return mapper.toResponse(saved);
    }

    public List<NoteResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NoteResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));
    }

    public NoteResponseTo update(Long id, NoteRequestTo request) {
        Note note = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));
        mapper.updateEntityFromDto(request, note);
        repository.update(note);
        return mapper.toResponse(note);
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Note not found with id: " + id);
        }
    }
}