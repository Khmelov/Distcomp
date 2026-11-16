package by.bsuir.rest.repository;

import by.bsuir.rest.entity.Identifiable;

import java.util.Optional;

public interface EntityRepository<T extends Identifiable, F> {

    T save(T entity);

    Optional<T> findById(Long id);

    boolean existsById(Long id);

    boolean deleteById(Long id);

    PageResult<T> find(F filter, PageQuery pageQuery);
}
