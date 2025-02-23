package by.kapinskiy.Distcomp.repositories;

import by.kapinskiy.Distcomp.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IssuesRepository extends JpaRepository<Issue, Long> {
    boolean existsByTitle(String title);
}
