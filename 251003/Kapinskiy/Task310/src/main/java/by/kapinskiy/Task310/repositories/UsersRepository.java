package by.kapinskiy.Task310.repositories;

import by.kapinskiy.Task310.models.User;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {
    User save(User user);
    void delete(User user);
    void deleteById(long id);
    Optional<User> findById(long id);
    List<User> findAll();
    boolean existsById(long id);

    boolean existsByLogin(String login);
}
