package org.example.discussion.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.discussion.dto.requestDto.MessageRequestTo;
import org.example.discussion.dto.responseDto.MessageResponseTo;
import org.example.discussion.dto.updateDto.MessageUpdateTo;
import org.example.discussion.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public Collection<MessageResponseTo> getAll() {
        return messageService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@RequestBody @Valid MessageRequestTo input) {
        return messageService.create(input);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseTo update(@RequestBody @Valid MessageUpdateTo input) {
        try {
            return messageService.update(input);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable long id) {
        try {
            return messageService.getById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@PathVariable long id) {
        return messageService.delete(id);
    }
}
