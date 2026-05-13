package by.bsuir.distcomp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CrudService<T> {
    T create(T dto);
    T get(Long id);
    List<T> findAll(Pageable pageable);
    T update(Long id, T dto);
    void delete(Long id);
}
