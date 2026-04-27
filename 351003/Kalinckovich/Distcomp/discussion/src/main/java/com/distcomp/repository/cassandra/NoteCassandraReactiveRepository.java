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

    

    /**
     * Find by note ID only (scans all topicIds in country)
     * ALLOW FILTERING is OK for SELECT
     */
    @Query("SELECT * FROM tbl_note WHERE country = ?0 AND id = ?1 ALLOW FILTERING")
    Mono<Note> findByNoteId(String country, Long id);

    /**
     * Find by note ID only (convenience method with default country)
     */
    @Query("SELECT * FROM tbl_note WHERE country = 'default' AND id = ?0 ALLOW FILTERING")
    Mono<Note> findByNoteId(Long id);

    /**
     * Find all by country and topicId
     */
    Flux<Note> findByKeyCountryAndKeyTopicId(String country, Long topicId, Pageable pageable);

    /**
     * Find all by country
     */
    Flux<Note> findByKeyCountry(String country, Pageable pageable);

    

    /**
     * Delete by note ID only
     * FIX: Cannot use ALLOW FILTERING with DELETE
     * Strategy: First find by ID, then delete with full key
     */
    @Query("SELECT * FROM tbl_note WHERE country = 'default' AND id = ?0 ALLOW FILTERING")
    Mono<Note> findFirstByNoteId(Long id);

    /**
     * Delete by country and topicId (full partition key + clustering)
     * This is valid because we have all primary key components
     */
    @Query("DELETE FROM tbl_note WHERE country = ?0 AND topic_id = ?1")
    Mono<Void> deleteByCountryAndTopicId(String country, Long topicId);
}