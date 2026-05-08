package by.tracker.rest_api.repository;

import by.tracker.rest_api.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}