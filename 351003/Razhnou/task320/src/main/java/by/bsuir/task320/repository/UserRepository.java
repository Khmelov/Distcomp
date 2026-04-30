package by.bsuir.task320.repository;

import by.bsuir.task320.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Long id);
}
