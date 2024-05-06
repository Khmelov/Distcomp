package by.bsuir.dc.lab5.services.repos;

import by.bsuir.dc.lab5.entities.Editor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditorRepository extends JpaRepository<Editor, Long> {
    Editor findByLogin(String login);
}
