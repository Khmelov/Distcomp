package com.example.discussion.repository;

import com.example.discussion.model.Note;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends CassandraRepository<Note, Long> {
    List<Note> findByArticleId(Long articleId);
}