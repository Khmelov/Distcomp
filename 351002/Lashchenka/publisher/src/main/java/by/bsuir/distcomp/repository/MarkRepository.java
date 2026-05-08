package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
