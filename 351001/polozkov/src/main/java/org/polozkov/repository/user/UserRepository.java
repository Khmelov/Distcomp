package org.polozkov.repository.user;

import org.polozkov.entity.user.User;
import org.polozkov.exception.BadRequestException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    default User byId(Long id) {
        return findById(id).orElseThrow(() -> new BadRequestException("User with id "  + id + " does not exists "));
    }

}