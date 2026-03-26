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

    
    Flux<Note> findByKeyCountryAndKeyTopicId(String country, Long topicId, Pageable pageable);

    Mono<Long> countByKeyCountryAndKeyTopicId(String country, Long topicId);

    Flux<Note> findByKeyCountry(String country, Pageable pageable);

    
    @Query("DELETE FROM tbl_note WHERE country = ?0 AND topic_id = ?1")
    Mono<Void> deleteByCountryAndTopicId(String country, Long topicId);

    
    @Query("SELECT * FROM tbl_note WHERE id = ?0 ALLOW FILTERING")
    Mono<Note> findByNoteId(Long id);
}