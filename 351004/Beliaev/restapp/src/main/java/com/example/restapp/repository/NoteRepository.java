package com.example.restapp.repository;

import com.example.restapp.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteRepository extends InMemoryRepository<Note> {
}