package com.discussion.repository;

import com.discussion.entity.Note;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends CassandraRepository<Note, Note.NoteKey> {
    
	@Query("SELECT * FROM tbl_note WHERE id = ?0 ALLOW FILTERING")
	Optional<Note> findById(Long id);
	
    List<Note> findByKeyCountry(String country);
    
    Optional<Note> findByKeyCountryAndKeyTweetIdAndKeyId(String country, Long tweetId, Long id);
    
    @Query("SELECT * FROM tbl_note WHERE tweet_id = ?0 ALLOW FILTERING")
    List<Note> findByTweetId(Long tweetId);
    
	@Query("DELETE FROM tbl_note WHERE country = ?0 AND id = ?1")
	void deleteByCountryAndId(String country, Long id);
    
    boolean existsByKeyCountryAndKeyTweetIdAndKeyId(String country, Long tweetId, Long id);
}