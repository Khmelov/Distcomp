package com.sergey.orsik.repository;

import com.sergey.orsik.entity.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Обобщённый интерфейс для хранения и поиска сущностей (CRUD).
 *
 * @param <T> тип сущности, расширяющий Identifiable
 */
public interface CrudRepository<T extends Identifiable> {

    /**
     * Сохраняет сущность. Если id == null, генерируется новый id.
     *
     * @param entity сущность для сохранения
     * @return сохранённая сущность с установленным id
     */
    T save(T entity);

    /**
     * Ищет сущность по идентификатору.
     *
     * @param id идентификатор
     * @return Optional с сущностью или пустой
     */
    Optional<T> findById(Long id);

    /**
     * Возвращает все сущности данного типа.
     *
     * @return список всех сущностей
     */
    List<T> findAll();

    /**
     * Удаляет сущность по идентификатору.
     *
     * @param id идентификатор
     * @return true, если сущность была удалена, false если не найдена
     */
    boolean deleteById(Long id);

    /**
     * Проверяет наличие сущности с заданным id.
     *
     * @param id идентификатор
     * @return true, если сущность существует
     */
    boolean existsById(Long id);
}
