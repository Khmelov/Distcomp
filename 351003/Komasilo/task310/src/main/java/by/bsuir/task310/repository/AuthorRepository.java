package by.bsuir.task310.repository;

import by.bsuir.task310.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByLogin(String login);

    Optional<Author> findByLogin(String login);
}