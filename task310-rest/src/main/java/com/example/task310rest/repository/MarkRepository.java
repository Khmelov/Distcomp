package com.example.task310rest.repository;

import com.example.task310rest.entity.Mark;

import java.util.Optional;

/**
 * Репозиторий для сущности Mark
 * Расширяет базовый CrudRepository дополнительными методами поиска
 */
public interface MarkRepository extends CrudRepository<Mark, Long> {
    
    /**
     * Найти метку по имени
     * @param name имя метки
     * @return Optional с меткой или пустой Optional
     */
    Optional<Mark> findByName(String name);
    
    /**
     * Проверить существование метки по имени
     * @param name имя метки
     * @return true если существует, false иначе
     */
    boolean existsByName(String name);
}
