
package by.bsuir.task361.repo;

import by.bsuir.task361.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByLogin(String login);
}
