package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.NoteRequestTo;
import by.tracker.rest_api.dto.NoteResponseTo;
import by.tracker.rest_api.model.Note;
import by.tracker.rest_api.repository.CrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final CrudRepository<Note, Long> repository;
    private final NoteMapper mapper;

    public NoteResponseTo create(NoteRequestTo request) {
        Note entity = mapper.toEntity(request);
        Note saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<NoteResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public NoteResponseTo getById(Long id) {
        Note entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Note not found with id: " + id));
        return mapper.toResponse(entity);
    }

    public NoteResponseTo update(NoteRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID is required for update");
        }
        Note entity = mapper.toEntity(request);
        entity.setId(request.getId());
        Note updated = repository.update(entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}