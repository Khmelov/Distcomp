package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.entity.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Long id);

    Optional<Editor> findByLogin(String login);
}
