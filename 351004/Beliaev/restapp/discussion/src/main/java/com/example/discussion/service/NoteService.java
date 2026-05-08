package com.example.discussion.service;

import com.example.discussion.dto.request.NoteRequestTo;
import com.example.discussion.dto.response.NoteResponseTo;
import com.example.discussion.exception.EntityNotFoundException;
import com.example.discussion.mapper.NoteMapper;
import com.example.discussion.model.Note;
import com.example.discussion.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository repository;
    private final NoteMapper mapper;

    public NoteResponseTo create(NoteRequestTo request) {
        Note note = mapper.toEntity(request);
        // Генерация Long ID (простой вариант для лабы)
        note.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
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
        note.setId(id); // Cassandra save() работает как upsert
        Note saved = repository.save(note);
        return mapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Note not found with id: " + id);
        }
        repository.deleteById(id);
    }
}