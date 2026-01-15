package com.example.task310rest.repository;

import com.example.task310rest.entity.User;

import java.util.Optional;

/**
 * Репозиторий для сущности User
 * Расширяет базовый CrudRepository дополнительными методами поиска
 */
public interface UserRepository extends CrudRepository<User, Long> {
    
    /**
     * Найти пользователя по логину
     * @param login логин пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findByLogin(String login);
    
    /**
     * Проверить существование пользователя по логину
     * @param login логин пользователя
     * @return true если существует, false иначе
     */
    boolean existsByLogin(String login);
}
