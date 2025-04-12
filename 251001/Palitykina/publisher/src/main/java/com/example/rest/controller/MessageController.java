package com.example.rest.controller;

import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;
import com.example.rest.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public Collection<MessageResponseTo> getAll() { return messageService.getAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@RequestBody @Valid MessageRequestTo input) { return messageService.create(input); }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseTo update(@RequestBody @Valid MessageUpdateTo input) {
        try{ return messageService.update(input); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public MessageResponseTo get(@PathVariable long id) {
        try{ return messageService.get(id); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        boolean deleted = messageService.delete(id);
        if (!deleted) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
