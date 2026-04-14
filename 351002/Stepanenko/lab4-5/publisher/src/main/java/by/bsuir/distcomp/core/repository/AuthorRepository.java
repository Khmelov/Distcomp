package by.bsuir.distcomp.core.repository;

import by.bsuir.distcomp.core.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByLogin(String login);
    Optional<Author> findByLogin(String login);
}