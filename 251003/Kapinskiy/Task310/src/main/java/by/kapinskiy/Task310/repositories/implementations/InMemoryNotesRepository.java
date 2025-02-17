package by.kapinskiy.Task310.repositories.implementations;

import by.kapinskiy.Task310.models.Note;
import by.kapinskiy.Task310.models.Note;
import by.kapinskiy.Task310.repositories.NotesRepository;
import by.kapinskiy.Task310.repositories.NotesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class InMemoryNotesRepository implements NotesRepository {
    private final ConcurrentHashMap<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();


    @Override
    public void delete(Note note) {
        notes.remove(note.getId());
    }

    @Override
    public Note save(Note note) {
        long id = note.getId();
        if (!notes.containsKey(id)) {
            id = idGenerator.incrementAndGet();
            note.setId(id);
        }

        notes.put(id, note);
        return note;
    }

    @Override
    public void deleteById(long id) {
        notes.remove(id);
    }

    @Override
    public Optional<Note> findById(long id) {
        if (notes.containsKey(id)) {
            return Optional.of(notes.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Note> findAll() {
        return notes.values().stream().toList();
    }

    @Override
    public boolean existsById(long id) {
        return notes.containsKey(id);
    }

}
