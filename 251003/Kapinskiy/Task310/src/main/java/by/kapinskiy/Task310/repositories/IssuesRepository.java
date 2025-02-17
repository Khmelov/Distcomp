package by.kapinskiy.Task310.repositories;

import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.models.User;

import java.util.List;
import java.util.Optional;

public interface IssuesRepository {
    Issue save(Issue issue);
    void delete(Issue issue);
    void deleteById(long id);
    Optional<Issue> findById(long id);
    List<Issue> findAll();
    boolean existsById(long id);
    boolean existsByTitle(String title);

}
