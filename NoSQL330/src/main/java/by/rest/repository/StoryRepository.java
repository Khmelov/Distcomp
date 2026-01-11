package by.rest.repository;

import by.rest.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoryRepository extends JpaRepository<Story, Long>, JpaSpecificationExecutor<Story> {
}