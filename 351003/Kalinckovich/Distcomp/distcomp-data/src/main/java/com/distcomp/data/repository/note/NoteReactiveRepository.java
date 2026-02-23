package com.distcomp.data.repository.note;

import com.distcomp.model.note.Note;
import com.distcomp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NoteReactiveRepository extends R2dbcRepository<Note, Long> {

    Flux<Note> findByTopicId(Long topicId, Pageable pageable);

    Flux<Note> findAllBy(Pageable pageable);

    @Modifying
    @Query("DELETE FROM tbl_notes WHERE id = :id")
    Mono<Integer> deleteNoteById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM tbl_note WHERE topic_id = :topicId")
    Mono<Integer> deleteByTopicId(Long topicId);
}
