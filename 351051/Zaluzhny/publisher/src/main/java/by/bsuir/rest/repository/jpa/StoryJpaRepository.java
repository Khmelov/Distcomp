package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoryJpaRepository extends JpaRepository<Story, Long>, JpaSpecificationExecutor<Story> {
}
