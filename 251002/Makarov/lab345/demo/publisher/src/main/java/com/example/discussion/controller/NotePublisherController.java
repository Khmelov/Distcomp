package com.example.discussion.controller;


import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.dto.NoteResponseTo;
import com.example.discussion.service.NotePublisherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@EnableKafka
@RestController
@RequestMapping("/api/v1.0/notes")
public class NotePublisherController {


    private final NotePublisherService noteService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public NotePublisherController(NotePublisherService noteService, RedisTemplate<String, Object> redisTemplate) {
        this.noteService = noteService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAllNotes() {
        try {
            String cacheKey = "allNotes";
            List<NoteResponseTo> notes = (List<NoteResponseTo>) redisTemplate.opsForValue().get(cacheKey);
            if (notes == null) {
                notes = noteService.findAll();
                redisTemplate.opsForValue().set(cacheKey, notes, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(notes, HttpStatus.OK);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            String cacheKey = "note:" + id;
            NoteResponseTo note = (NoteResponseTo) redisTemplate.opsForValue().get(cacheKey);
            if (note == null) {
                note = noteService.findById(id);
                if (note != null) {
                    redisTemplate.opsForValue().set(cacheKey, note, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("Note does not exist", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(note, HttpStatus.OK);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>("Note does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody @Valid NoteRequestTo noteRequestTo) {
        try {
            NoteResponseTo createdNote = noteService.save(noteRequestTo);
            redisTemplate.delete("allNotes");
            return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateNote(@RequestBody @Valid NoteRequestTo noteRequestTo) {
        try {
            NoteResponseTo updatedNote = noteService.update(noteRequestTo);
            if (updatedNote != null) {
                redisTemplate.delete("allNotes");
                redisTemplate.delete("note:" + updatedNote.getId());
                return new ResponseEntity<>(updatedNote, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            noteService.deleteById(id);
            redisTemplate.delete("allNotes");
            redisTemplate.delete("note:" + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TimeoutException e) {
            return new ResponseEntity<>("Request timeout: " + e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
