package by.distcomp.app.repository;

import by.distcomp.app.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    void deleteByArticleId(Long articleId);
}
