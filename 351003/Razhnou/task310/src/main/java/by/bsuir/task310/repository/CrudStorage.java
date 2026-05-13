package by.bsuir.task310.repository;

import java.util.List;
import java.util.Optional;

public interface CrudStorage<T> {
    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T entity);

    T update(T entity);

    void deleteById(Long id);

    boolean existsById(Long id);
}
