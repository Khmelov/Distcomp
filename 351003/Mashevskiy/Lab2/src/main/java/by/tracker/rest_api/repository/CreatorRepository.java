package by.tracker.rest_api.repository;

import by.tracker.rest_api.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    boolean existsByLogin(String login);
}