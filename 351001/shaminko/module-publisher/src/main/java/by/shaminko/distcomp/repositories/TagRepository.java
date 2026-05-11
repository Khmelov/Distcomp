package by.shaminko.distcomp.repositories;

import by.shaminko.distcomp.entities.Tag;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TagRepository extends Repo<Tag> {
    Optional<Tag> findByName(String name);
}
