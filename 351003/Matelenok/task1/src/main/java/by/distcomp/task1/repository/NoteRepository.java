package by.distcomp.task1.repository;

import by.distcomp.task1.model.Note;

public interface NoteRepository extends CrudRepository<Note, Long>{
    void deleteByArticleId(Long articleId);
}
