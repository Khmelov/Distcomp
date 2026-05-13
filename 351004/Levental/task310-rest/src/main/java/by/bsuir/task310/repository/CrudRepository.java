package by.bsuir.task310.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    T update(Long id, T entity);
    void delete(Long id);
    boolean existsById(Long id);
    void clear();
}
