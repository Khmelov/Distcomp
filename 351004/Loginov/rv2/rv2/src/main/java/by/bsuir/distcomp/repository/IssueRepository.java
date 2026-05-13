package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Long id);
}
