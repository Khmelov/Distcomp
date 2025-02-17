package by.kapinskiy.Task310.repositories.implementations;


import by.kapinskiy.Task310.models.User;
import by.kapinskiy.Task310.repositories.UsersRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class InMemoryUsersRepository implements UsersRepository {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private AtomicLong idGenerator = new AtomicLong();

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public User save(User user) {
        long id = user.getId();
        if (!users.containsKey(id)) {
            id = idGenerator.incrementAndGet();
            user.setId(id);
        }

        users.put(id, user);
        return user;
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findById(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public boolean existsById(long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean existsByLogin(String login) {
        return users.values().stream().anyMatch(user -> user.getLogin().equals(login));
    }


}
