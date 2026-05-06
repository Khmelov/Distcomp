package by.boukhvalova.distcomp.repositories;

import by.shaminko.distcomp.entities.Sticker;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StickerRepository extends Repo<Sticker> {
    Optional<Sticker> findByName(String name);
}
