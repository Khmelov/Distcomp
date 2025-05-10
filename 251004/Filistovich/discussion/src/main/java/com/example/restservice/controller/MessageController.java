package com.example.restservice.controller;

import com.example.restservice.dto.MessageCreateDto;
import com.example.restservice.dto.MessageResponseDto;
import com.example.restservice.dto.NewsResponseDto;
import com.example.restservice.mapper.MessageMapper;
import com.example.restservice.model.Message;
import com.example.restservice.repository.MessageRepository;
import com.example.restservice.service.MessageService;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
public class MessageController {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private static final String BASE_NEWS_URL = "http://localhost:24110/api/v1.0/news";

    @Autowired
    public MessageController(MessageMapper messageMapper, MessageRepository messageRepository, MessageService messageService) {
        this.messageMapper = messageMapper;
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }
    @GetMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponseDto> getMarkers() {
        return messageService.findAll().stream().map(messageMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageResponseDto> createMarker(@RequestBody MessageCreateDto inputDto) {

        Message entity = messageMapper.toEntity(inputDto);
        RestTemplate restTemplate = new RestTemplate();
        String apiAUrl = BASE_NEWS_URL + "/" + inputDto.getNewsId();
        if (restTemplate.getForObject(apiAUrl, NewsResponseDto.class) == null) {
            return new ResponseEntity<>(messageMapper.toDto(entity), HttpStatus.BAD_REQUEST);
        }

        Message saved = messageService.save(entity);
        return new ResponseEntity<>(messageMapper.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageResponseDto> getMarkerById(@PathVariable Long id) {
        if (messageService.findById(id) == null) {
            return new ResponseEntity<>(new MessageResponseDto(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(messageMapper.toDto(messageService.findById(id)), HttpStatus.OK);
    }

    @PutMapping("/messages")
    public ResponseEntity<MessageResponseDto> updateMarker(@RequestBody @Valid MessageCreateDto in) {
        Message message = messageService.findById(in.getId());
        if (message == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Message newMessage = messageMapper.toEntity(in);
            Message updated = messageService.update(newMessage);
            return ResponseEntity.ok(messageMapper.toDto(updated));
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
