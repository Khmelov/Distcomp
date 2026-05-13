package com.bsuir.distcomp.service;

import java.util.List;

public interface CrudService<T, R> {

    R create(T t);
    List<R> getAll();
    void delete(Long id);
    R update(Long id, T t);
    R getById(Long id);
}
