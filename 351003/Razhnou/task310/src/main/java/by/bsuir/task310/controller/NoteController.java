package by.bsuir.task310.controller;

import by.bsuir.task310.dto.request.NoteRequestTo;
import by.bsuir.task310.dto.response.NoteResponseTo;
import by.bsuir.task310.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponseTo> create(@RequestBody NoteRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.create(request));
    }

    @GetMapping
    public List<NoteResponseTo> findAll() {
        return noteService.findAll();
    }

    @GetMapping("/{id}")
    public NoteResponseTo findById(@PathVariable Long id) {
        return noteService.findById(id);
    }

    @GetMapping("/byTweet/{tweetId}")
    public List<NoteResponseTo> findByStoryId(@PathVariable Long tweetId) {
        return noteService.findByStoryId(tweetId);
    }

    @PutMapping
    public NoteResponseTo update(@RequestBody NoteRequestTo request) {
        return noteService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
