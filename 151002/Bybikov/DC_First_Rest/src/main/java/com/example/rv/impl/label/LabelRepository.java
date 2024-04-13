package com.example.rv.impl.label;

import com.example.rv.api.MemRepository.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LabelRepository extends MemoryRepository<Label> {
    @Override
    public Optional<Label> save(Label entity) {
        entity.id = ids.incrementAndGet();
        map.put(entity.getId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Label> update(Label label) {
        Long id = label.getId();
        Label memRepLabel = map.get(id);

        if (memRepLabel != null &&
                label.getName().length() > 1 &&
                label.getName().length() < 33
        ) {
            memRepLabel = label;
        } else return Optional.empty();

        return Optional.of(memRepLabel);
    }

    @Override
    public boolean delete(Label entity) {
        return map.remove(entity.getId(), entity);
    }
}
