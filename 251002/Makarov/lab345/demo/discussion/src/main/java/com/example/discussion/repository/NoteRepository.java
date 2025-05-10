package com.example.discussion.repository;

import com.example.discussion.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NoteRepository extends MongoRepository<Note, Long> {
}