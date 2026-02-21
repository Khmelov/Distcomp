package by.distcomp.task1.repository;

import by.distcomp.task1.model.Note;
import org.springframework.stereotype.Repository;

@Repository
public class NoteRepositoryImpl extends CrudRepositoryImpl<Note>
        implements NoteRepository {

    @Override
    protected Long getId(Note note) {
        return note.getId();
    }

    @Override
    protected void setId(Note note, Long id) {
        note.setId(id);
    }
    @Override
    public void deleteByArticleId(Long articleId) {
        storage.values().removeIf(note ->
                note.getArticle() != null &&
                        note.getArticle().getId().equals(articleId)
        );
    }
}
