package by.bsuir.task310.repository;

import by.bsuir.task310.entity.Story;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoryRepository extends AbstractInMemoryStorage<Story> {
    @Override
    protected Long getId(Story entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Story entity, Long id) {
        entity.setId(id);
    }

    public List<Story> findByEditorId(Long editorId) {
        return findAll().stream()
                .filter(story -> editorId.equals(story.getEditorId()))
                .toList();
    }
}
