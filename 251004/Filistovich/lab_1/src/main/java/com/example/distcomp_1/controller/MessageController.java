package com.example.distcomp_1.controller;

import com.example.distcomp_1.mapper.MessageDto;
import com.example.distcomp_1.mdoel.Message;
import com.example.distcomp_1.repository.MessageRepository;
import com.example.distcomp_1.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
public class MessageController {

    private final MessageDto messageDto;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageDto messageDto, MessageRepository messageRepository, MessageService messageService) {
        this.messageDto = messageDto;
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }
    @GetMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<Message.Out> getMarkers() {
        return messageRepository.getMessages()
                .stream()
                .map(messageDto::Out)
                .collect(Collectors.toList());
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message.Out createMarker(@RequestBody @Valid Message.In inputDto) {
        Message entity = messageDto.In(inputDto);
        Message saved = messageService.save(entity);
        return messageDto.Out(saved);
    }

    @GetMapping("/messages/{id}")
    public Message.Out getMarkerById(@PathVariable Long id) {
        return messageDto.Out(messageService.findById(id));
    }

    @PutMapping("/messages")
    public ResponseEntity<Message.Out> updateMarker(@RequestBody @Valid Message.In in) {
        Message message = messageService.findById(in.getId());
        if (message == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Message newMessage = messageDto.In(in);
            Message updated = messageService.update(newMessage);
            return ResponseEntity.ok(messageDto.Out(updated));
        }
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        if (messageService.findById(id) != null) {
            messageService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
