package by.molchan.Publisher.repositories;

import by.molchan.Publisher.models.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LabelsRepository extends JpaRepository<Label, Long> {
    boolean existsByName(String name);
    List<Label> findByNameIn(Set<String> names);
}
