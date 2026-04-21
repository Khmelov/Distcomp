package com.distcomp.repository.cassandra;

import com.distcomp.model.note.Note;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface NoteCassandraReactiveRepository extends ReactiveCassandraRepository<Note, Note.NoteKey> {

    @Query("SELECT * FROM tbl_note WHERE country = ?0 AND id = ?1 LIMIT 1 ALLOW FILTERING")
    Mono<Note> findByNoteId(String country, Long id);

    @Query("SELECT * FROM tbl_note WHERE country = 'default' AND id = ?0 LIMIT 1 ALLOW FILTERING")
    Mono<Note> findByNoteId(Long id);

    @Query("SELECT * FROM tbl_note WHERE country = 'default' AND id = ?0 LIMIT 1 ALLOW FILTERING")
    Mono<Note> findFirstByNoteId(Long id);

    Flux<Note> findByKeyCountryAndKeyTopicId(String country, Long topicId, Pageable pageable);

    Flux<Note> findByKeyCountry(String country, Pageable pageable);

    @Query("DELETE FROM tbl_note WHERE country = ?0 AND topic_id = ?1")
    Mono<Void> deleteByCountryAndTopicId(String country, Long topicId);
}