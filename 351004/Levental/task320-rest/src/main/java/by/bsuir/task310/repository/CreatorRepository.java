package by.bsuir.task310.repository;

import by.bsuir.task310.domain.Creator;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreatorRepository extends InMemoryCrudRepository<Creator> {
    public Optional<Creator> findByLogin(String login) {
        return storage.values().stream()
                .filter(c -> c.getLogin().equals(login))
                .findFirst();
    }
}
