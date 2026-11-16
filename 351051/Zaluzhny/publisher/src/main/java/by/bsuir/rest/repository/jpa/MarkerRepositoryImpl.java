package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Marker;
import by.bsuir.rest.entity.Story;
import by.bsuir.rest.repository.MarkerFilter;
import by.bsuir.rest.repository.MarkerRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class MarkerRepositoryImpl extends JpaEntityRepository<Marker, MarkerFilter> implements MarkerRepository {

    private final EntityManager entityManager;

    public MarkerRepositoryImpl(MarkerJpaRepository jpa, EntityManager entityManager) {
        super(jpa);
        this.entityManager = entityManager;
    }

    @Override
    protected Specification<Marker> specification(MarkerFilter filter) {
        return EntitySpecifications.markers(filter);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!existsById(id)) {
            return false;
        }
        List<Story> stories = entityManager.createQuery(
                        "select distinct story from Story story join story.markers link where link.markerId = :markerId",
                        Story.class)
                .setParameter("markerId", id)
                .getResultList();
        for (Story story : stories) {
            Set<Long> remaining = story.getMarkerIds().stream()
                    .filter(markerId -> !id.equals(markerId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            story.setMarkerIds(remaining);
        }
        entityManager.flush();
        return super.deleteById(id);
    }
}
