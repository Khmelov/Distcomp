package by.distcomp.app.repository;

import by.distcomp.app.model.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    Optional<Sticker> findByName(String name);
}
