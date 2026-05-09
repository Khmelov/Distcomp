package by.distcomp.app.repository;

import by.distcomp.app.model.Note;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends CassandraRepository<Note, Long> {

    List<Note> findByArticleId(Long articleId);

        @AllowFiltering
        @Query("DELETE FROM tbl_note WHERE article_id = ?0")
        void deleteByArticleId(Long articleId);

}