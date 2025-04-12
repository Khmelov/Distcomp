package by.bsuir.distcomp.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    Optional<T> findById(Long id);
    List<T> findAll();
    T create(T author);
    T update(T author);
    void deleteById(Long id);
}
