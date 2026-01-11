package by.rest.publisher.repository;

import by.rest.publisher.domain.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EditorRepository extends JpaRepository<Editor, Long> {
    Optional<Editor> findByLogin(String login);
}