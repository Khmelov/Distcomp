package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.model.Writer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriterRepository extends JpaRepository<Writer, Long> {
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Long id);
    Optional<Writer> findByLogin(String login);
}
