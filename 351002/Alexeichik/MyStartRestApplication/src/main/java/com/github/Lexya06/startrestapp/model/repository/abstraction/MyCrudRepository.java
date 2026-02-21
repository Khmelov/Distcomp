package com.github.Lexya06.startrestapp.model.repository.abstraction;

import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;

import java.util.*;

public abstract class MyCrudRepository<T extends BaseEntity> implements MyCrudRepositoryImpl<T> {
    protected abstract  Map<Long, T> getBaseEntityMap();
    @Override
    public Optional<T> findById(Long id) {
        Optional<T> result;
        result = Optional.ofNullable(getBaseEntityMap().get(id));
        return result;
    }

    @Override
    public T save(T baseEntity) {

        long id = baseEntity.getId() == null ? 1L : baseEntity.getId();
        if (!getBaseEntityMap().containsKey(id)) {
            // casting to long because 1L
            id = getBaseEntityMap().size() + 1L;
        }

        baseEntity.setId(id);
        getBaseEntityMap().put(id, baseEntity);
        return baseEntity;
    }



    @Override
    public void deleteById(Long id) {
        getBaseEntityMap().remove(id);
    }

    @Override
    public Set<T> findAll() {
        return new HashSet<>(getBaseEntityMap().values());
    }
}
