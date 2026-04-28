package com.example.publisher.repository;

import com.example.publisher.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByTweetId(Long tweetId);
}