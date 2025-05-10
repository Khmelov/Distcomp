package com.example.restservice.controller;

import com.example.restservice.dto.MessageCreateDto;
import com.example.restservice.dto.MessageResponseDto;
import com.example.restservice.mapper.MessageMapper;
import com.example.restservice.model.Message;
import com.example.restservice.service.MessageService;
import com.example.restservice.service.NewsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final NewsService newsService;

    private final RedisTemplate<String, Object> redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_NAME = "messages";
    private static final long CACHE_TTL_MINUTES = 30;
    /*@Autowired
    public MessageController(MessageService messageService, MessageMapper messageMapper, NewsService newsService) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.newsService = newsService;
    }*/


    @GetMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    //@Cacheable(value = CACHE_NAME, key = "'allMessages'")
    public List<MessageResponseDto> getMessages() {
        String key = "allMessages";
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<MessageResponseDto>>() {});
        }

        List<MessageResponseDto> authors = messageService.findAll().stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, authors, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return messageService.findAll().stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageResponseDto> createMessage(@RequestBody @Valid MessageCreateDto inputDto) {

        Message entity = messageMapper.toEntity(inputDto, newsService.findById(inputDto.getNewsId()));
        /*if (authorRepository.existsByLogin(entity.getLogin())) {
            return new ResponseEntity<>(authorMapper.toDto(entity), HttpStatus.FORBIDDEN);
        }*/
        Message saved = messageService.save(entity);
        redisTemplate.delete("allMessages");
        redisTemplate.opsForValue().set(
                "message:" + inputDto.getId(),
                messageMapper.toDto(saved),
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        return new ResponseEntity<>(messageMapper.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/messages/{id}")
    public MessageResponseDto getMessageById(@PathVariable Long id) {
        String key = "message:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, MessageResponseDto.class);
        }

        return messageMapper.toDto(messageService.findById(id));
    }

    @PutMapping("/messages")
    public ResponseEntity<MessageResponseDto> updateMessage(@RequestBody @Valid MessageCreateDto in) {
        redisTemplate.delete("allMessages");
        redisTemplate.delete("message:" + in.getId());
        redisTemplate.opsForValue().set(
                "message:" + in.getId(),
                messageMapper.toDto(messageMapper.toEntity(in, newsService.findById(in.getNewsId()))),
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        Message message = messageService.findById(in.getId());
        if (message == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Message newMessage = messageMapper.toEntity(in, newsService.findById(in.getNewsId()));
            Message updated = messageService.update(newMessage);
            return ResponseEntity.ok(messageMapper.toDto(updated));
        }
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {

        redisTemplate.delete("allMessages");
        redisTemplate.delete("message:" + id);

        if (messageService.findById(id) != null) {
            messageService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
