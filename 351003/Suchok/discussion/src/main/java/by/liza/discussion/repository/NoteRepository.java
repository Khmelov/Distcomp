package by.liza.discussion.repository;

import by.liza.discussion.model.Note;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends CassandraRepository<Note, Long> {

    @AllowFiltering
    List<Note> findByArticleId(Long articleId);
}