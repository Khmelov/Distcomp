package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Story;
import by.bsuir.rest.repository.StoryRepository;
import by.bsuir.rest.service.StorySearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class StoryRepositoryImpl extends JpaEntityRepository<Story, StorySearchCriteria> implements StoryRepository {

    public StoryRepositoryImpl(StoryJpaRepository jpa) {
        super(jpa);
    }

    @Override
    protected Specification<Story> specification(StorySearchCriteria filter) {
        return EntitySpecifications.stories(filter);
    }
}
