package by.bsuir.task340.publisher.repository;

import by.bsuir.task340.publisher.domain.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface CreatorRepository extends JpaRepository<Creator, Long>, JpaSpecificationExecutor<Creator> {
    Optional<Creator> findByLogin(String login);
}
