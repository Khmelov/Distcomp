package by.shaminko.distcomp.repositories;

import by.shaminko.distcomp.entities.Editor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditorRepository extends Repo<Editor> {
    Optional<Editor> findByLogin(String login);
}
