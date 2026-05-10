package by.bsuir.task310.repository;

import by.bsuir.task310.entity.Editor;
import org.springframework.stereotype.Repository;

@Repository
public class EditorRepository extends AbstractInMemoryStorage<Editor> {
    @Override
    protected Long getId(Editor entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Editor entity, Long id) {
        entity.setId(id);
    }
}
