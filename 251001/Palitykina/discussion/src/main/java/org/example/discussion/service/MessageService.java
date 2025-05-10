package org.example.discussion.service;

import org.example.discussion.dto.requestDto.MessageRequestTo;
import org.example.discussion.dto.responseDto.MessageResponseTo;
import org.example.discussion.dto.updateDto.MessageUpdateTo;
import org.example.discussion.entity.Message;
import org.example.discussion.mapper.MessageMapper;
import org.example.discussion.repository.MessageRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;
    private final MessageMapper messageMapper;

    public List<MessageResponseTo> getAll() {
        return messageRepo
                .getAll()
                .map(messageMapper::ToMessageResponseTo)
                .toList();
    }

    public MessageResponseTo getById( long id) {
        return messageRepo
                .findByCountryAndId("US", id)
                .map(messageMapper::ToMessageResponseTo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    public MessageResponseTo create(MessageRequestTo input) {
        Message message = messageMapper.ToMessage(input);

        return messageRepo
                .create(message)
                .map(messageMapper::ToMessageResponseTo)
                .orElseThrow();
    }

    public MessageResponseTo update(MessageUpdateTo input) {
        Message message = messageMapper.ToMessage(input);
        return messageRepo
                .update(message)
                .map(messageMapper::ToMessageResponseTo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void delete(long id) {
        if(!messageRepo.existsByCountryAndId("US", id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
            messageRepo.deleteByCountryAndId("US", id);

    }
}
