package by.bsuir.task310.repository;

import by.bsuir.task310.domain.Sticker;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StickerRepository extends InMemoryCrudRepository<Sticker> {
    public Optional<Sticker> findByName(String name) {
        return storage.values().stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
    }
}
