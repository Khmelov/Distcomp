package by.boukhvalova.distcomp.repositories;

import by.shaminko.distcomp.entities.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends Repo<User> {
    Optional<User> findByLogin(String login);
}
