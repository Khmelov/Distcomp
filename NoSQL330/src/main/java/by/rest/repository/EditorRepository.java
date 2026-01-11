package by.rest.repository;

import by.rest.domain.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EditorRepository extends JpaRepository<Editor, Long> {
    Optional<Editor> findByLogin(String login);
}