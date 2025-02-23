package by.kapinskiy.Distcomp.repositories;

import by.kapinskiy.Distcomp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
}
