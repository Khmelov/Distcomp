package by.kapinskiy.Task310.repositories;

import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.models.Note;

import java.util.List;
import java.util.Optional;

public interface NotesRepository {
    Note save(Note note);
    void delete(Note note);
    void deleteById(long id);
    Optional<Note> findById(long id);
    List<Note> findAll();
    boolean existsById(long id);

}
