package by.distcomp.app.repository;

import by.distcomp.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
}
