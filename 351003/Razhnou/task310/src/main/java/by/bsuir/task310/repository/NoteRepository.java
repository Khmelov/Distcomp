package by.bsuir.task310.repository;

import by.bsuir.task310.entity.Note;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoteRepository extends AbstractInMemoryStorage<Note> {
    @Override
    protected Long getId(Note entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Note entity, Long id) {
        entity.setId(id);
    }

    public List<Note> findByStoryId(Long storyId) {
        return findAll().stream()
                .filter(note -> storyId.equals(note.getStoryId()))
                .toList();
    }

    public void deleteByStoryId(Long storyId) {
        findByStoryId(storyId).forEach(note -> deleteById(note.getId()));
    }
}
