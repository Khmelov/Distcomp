package org.example.discussion.service;

import org.example.discussion.dto.requestDto.MessageRequestTo;
import org.example.discussion.dto.responseDto.MessageResponseTo;
import org.example.discussion.dto.updateDto.MessageUpdateTo;
import org.example.discussion.entity.Message;
import org.example.discussion.mapper.MessageMapper;
import org.example.discussion.repository.MessageRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElse(null);
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
                .orElseThrow();
    }

    public boolean delete(long id) {
        return messageRepo.deleteByCountryAndId("US", id);
    }
}
