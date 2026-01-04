package com.aitor.discussion.controller;

import com.aitor.publisher.dto.MessageRequestTo;
import com.aitor.discussion.service.MessageService;
import com.aitor.publisher.dto.MessageResponseTo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("messages")
class MessageController {
    private final MessageService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MessageResponseTo add(@RequestBody @Valid MessageRequestTo request){
        return service.add(request);
    }

    @PutMapping("/{id}")
    public MessageResponseTo set(@PathVariable Long id, @RequestBody @Valid MessageRequestTo request){
        return service.set(id, request);
    }

    @GetMapping("/{id}")
    public MessageResponseTo get(@PathVariable Long id){
        return service.get(id);
    }

    @GetMapping
    public List<MessageResponseTo> getAll(){
        return service.getAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public MessageResponseTo remove(@PathVariable Long id){
        return service.remove(id);
    }
}
