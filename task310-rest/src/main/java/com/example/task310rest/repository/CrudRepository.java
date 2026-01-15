package com.example.task310rest.repository;

import java.util.List;
import java.util.Optional;

/**
 * Обобщенный интерфейс репозитория для CRUD операций
 * @param <T> тип сущности
 * @param <ID> тип идентификатора
 */
public interface CrudRepository<T, ID> {
    
    /**
     * Сохранить новую сущность
     * @param entity сущность для сохранения
     * @return сохраненная сущность с присвоенным ID
     */
    T save(T entity);
    
    /**
     * Обновить существующую сущность
     * @param entity сущность для обновления
     * @return обновленная сущность
     */
    T update(T entity);
    
    /**
     * Найти сущность по ID
     * @param id идентификатор
     * @return Optional с сущностью или пустой Optional
     */
    Optional<T> findById(ID id);
    
    /**
     * Найти все сущности
     * @return список всех сущностей
     */
    List<T> findAll();
    
    /**
     * Удалить сущность по ID
     * @param id идентификатор
     * @return true если удаление успешно, false если сущность не найдена
     */
    boolean deleteById(ID id);
    
    /**
     * Проверить существование сущности по ID
     * @param id идентификатор
     * @return true если существует, false иначе
     */
    boolean existsById(ID id);
}
