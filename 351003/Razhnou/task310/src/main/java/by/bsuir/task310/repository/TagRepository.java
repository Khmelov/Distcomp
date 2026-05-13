package by.bsuir.task310.repository;

import by.bsuir.task310.entity.Tag;
import org.springframework.stereotype.Repository;

@Repository
public class TagRepository extends AbstractInMemoryStorage<Tag> {
    @Override
    protected Long getId(Tag entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Tag entity, Long id) {
        entity.setId(id);
    }
}
