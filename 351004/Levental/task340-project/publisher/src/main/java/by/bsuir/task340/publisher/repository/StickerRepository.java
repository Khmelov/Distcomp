package by.bsuir.task340.publisher.repository;

import by.bsuir.task340.publisher.domain.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface StickerRepository extends JpaRepository<Sticker, Long>, JpaSpecificationExecutor<Sticker> {
    Optional<Sticker> findByName(String name);
}
