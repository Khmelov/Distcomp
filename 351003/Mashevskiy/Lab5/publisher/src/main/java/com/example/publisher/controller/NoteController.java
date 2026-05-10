package com.example.publisher.controller;

import com.example.publisher.dto.NoteMessage;
import com.example.publisher.entity.Note;
import com.example.publisher.kafka.NoteProducer;
import com.example.publisher.repository.NoteRepository;
import com.example.publisher.repository.TweetRepository;
import com.example.publisher.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private NoteProducer noteProducer;

    @Autowired
    private CacheService cacheService;

    private static final String CACHE_KEY_PREFIX = "note:";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note) {
        if (!tweetRepository.existsById(note.getTweetId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tweet not found");
        }

        note.setState("PENDING");
        Note savedNote = noteRepository.save(note);

        NoteMessage message = new NoteMessage();
        message.setNoteId(savedNote.getId());
        message.setTweetId(savedNote.getTweetId());
        message.setText(savedNote.getContent());
        message.setState(savedNote.getState());

        noteProducer.sendToInTopic(message);

        cacheService.put(CACHE_KEY_PREFIX + savedNote.getId(), savedNote);

        return savedNote;
    }

    @GetMapping
    public Iterable<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Note getNote(@PathVariable Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        Note cached = (Note) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cacheService.put(cacheKey, note);
        return note;
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody Note note) {
        if (!noteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        note.setId(id);
        Note updated = noteRepository.save(note);
        cacheService.put(CACHE_KEY_PREFIX + id, updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        noteRepository.deleteById(id);
        cacheService.evict(CACHE_KEY_PREFIX + id);

        NoteMessage message = new NoteMessage();
        message.setNoteId(id);
        message.setState("DELETED");
        noteProducer.sendToInTopic(message);
    }
}