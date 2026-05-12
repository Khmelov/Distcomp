package com.distcomp.discussion.repository;

import com.distcomp.discussion.model.Note;
import com.distcomp.discussion.model.NoteKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends CassandraRepository<Note, NoteKey> {

    List<Note> findByIdTweetId(Long tweetId);

    @Query("SELECT * FROM tbl_note WHERE tweet_id = ?0 AND id = ?1 ALLOW FILTERING")
    Optional<Note> findByIdTweetIdAndId(Long tweetId, Long id);

    @Query("DELETE FROM tbl_note WHERE country = ?0 AND tweet_id = ?1 AND id = ?2")
    void deleteByCountryAndTweetIdAndId(String country, Long tweetId, Long id);
}