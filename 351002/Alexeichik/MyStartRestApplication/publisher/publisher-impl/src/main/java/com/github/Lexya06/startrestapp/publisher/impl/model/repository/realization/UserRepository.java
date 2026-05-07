package com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MyCrudRepositoryImpl<User> {
    Optional<User> findByLogin(String login);
}
