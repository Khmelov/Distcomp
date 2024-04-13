package com.example.rv.impl.creator;

import com.example.rv.api.MemRepository.MemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreatorRepository extends MemoryRepository<Creator> {

    @Override
    public Optional<Creator> save(Creator entity) {
        entity.id = ids.incrementAndGet();
        map.put(entity.getId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Creator> update(Creator creator) {
        Long id = creator.getId();
        Creator memRepCreator = map.get(id);

        if (creator.getLogin().length() > 1 && creator.getLogin().length() < 65 &&
                creator.getPassword().length() > 7 && creator.getPassword().length() < 129 &&
                creator.getFirstname().length() > 1 && creator.getFirstname().length() < 65 &&
                creator.getLastname().length() > 1 && creator.getLastname().length() < 65) {

            memRepCreator.setLogin(creator.getLogin());
            memRepCreator.setFirstname(creator.getFirstname());
            memRepCreator.setLastname(creator.getLastname());
            memRepCreator.setPassword(creator.getPassword());
        } else return Optional.empty();

        return Optional.of(memRepCreator);
    }

    @Override
    public boolean delete(Creator entity) {
        return map.remove(entity.getId(), entity);
    }
}
