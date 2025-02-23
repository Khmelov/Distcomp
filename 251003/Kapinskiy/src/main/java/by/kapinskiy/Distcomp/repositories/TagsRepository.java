package by.kapinskiy.Distcomp.repositories;

import by.kapinskiy.Distcomp.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);
    List<Tag> findByNameIn(Set<String> names);
}
