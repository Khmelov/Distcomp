package by.kapinskiy.Task310.repositories;

import by.kapinskiy.Task310.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagsRepository {
    Tag save(Tag tag);
    void delete(Tag tag);
    void deleteById(long id);
    Optional<Tag> findById(long id);
    List<Tag> findAll();
    boolean existsById(long id);
    boolean existsByName(String name);
}
