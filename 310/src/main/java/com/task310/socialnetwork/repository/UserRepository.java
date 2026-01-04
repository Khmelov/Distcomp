package com.task310.socialnetwork.repository;

import com.task310.socialnetwork.model.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
}