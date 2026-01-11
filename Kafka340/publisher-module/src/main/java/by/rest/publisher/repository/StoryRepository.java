package by.rest.publisher.repository;

import by.rest.publisher.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoryRepository extends JpaRepository<Story, Long>, JpaSpecificationExecutor<Story> {
}