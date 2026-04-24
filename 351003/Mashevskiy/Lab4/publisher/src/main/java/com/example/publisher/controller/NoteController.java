package com.example.publisher.controller;

import com.example.publisher.dto.NoteMessage;
import com.example.publisher.entity.Note;
import com.example.publisher.kafka.NoteProducer;
import com.example.publisher.repository.NoteRepository;
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
    private NoteProducer noteProducer;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note) {
        note.setState("PENDING");
        Note savedNote = noteRepository.save(note);

        NoteMessage message = new NoteMessage();
        message.setNoteId(savedNote.getId());
        message.setTweetId(savedNote.getTweetId());
        message.setText(savedNote.getContent());
        message.setState(savedNote.getState());

        noteProducer.sendToInTopic(message);

        return savedNote;
    }

    @GetMapping
    public Iterable<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Note getNote(@PathVariable Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody Note note) {
        if (!noteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        note.setId(id);
        return noteRepository.save(note);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        noteRepository.deleteById(id);
        NoteMessage message = new NoteMessage();
        message.setNoteId(id);
        message.setState("DELETED");
        noteProducer.sendToInTopic(message);

        System.out.println("Delete event sent to Kafka for note " + id);
    }
}