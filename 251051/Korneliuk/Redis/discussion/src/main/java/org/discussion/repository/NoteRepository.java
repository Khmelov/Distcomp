package org.discussion.repository;

import org.discussion.model.Note;
import org.discussion.model.NoteKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CassandraRepository<Note, NoteKey> {
}