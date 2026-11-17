package by.bsuir.discussion.controller;

import by.bsuir.discussion.dto.MessageRequestTo;
import by.bsuir.discussion.dto.MessageResponseTo;
import by.bsuir.discussion.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/messages", "/messages"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@RequestBody MessageRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAll(@RequestParam(required = false) Long storyId) {
        if (storyId == null) {
            return ResponseEntity.ok(messageService.getAll());
        }
        return ResponseEntity.ok(messageService.findByStoryId(storyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getById(id));
    }

    @PutMapping
    public ResponseEntity<MessageResponseTo> update(@RequestBody MessageRequestTo request) {
        return ResponseEntity.ok(messageService.update(null, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> update(@PathVariable Long id, @RequestBody MessageRequestTo request) {
        return ResponseEntity.ok(messageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
