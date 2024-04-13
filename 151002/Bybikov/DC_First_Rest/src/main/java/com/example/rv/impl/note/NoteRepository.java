package com.example.rv.impl.note;

import com.example.rv.api.MemRepository.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NoteRepository extends MemoryRepository<Note> {
    @Override
    public Optional<Note> save(Note entity) {
        entity.id = ids.incrementAndGet();
        map.put(entity.getId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Note> update(Note note) {
        Long id = note.getId();
        Note memRepNote = map.get(id);

        if (memRepNote != null &&
                note.getIssueId() != null &&
                note.getContent().length() > 1 &&
                note.getContent().length() < 2049
        ) {

            memRepNote = note;
        } else return Optional.empty();

        return Optional.of(memRepNote);
    }

    @Override
    public boolean delete(Note entity) {
        return map.remove(entity.getId(), entity);
    }
}
