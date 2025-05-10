package com.example.rest.service.MessageService;

import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;

import java.util.List;

public interface MessageService{
    public List<MessageResponseTo> getAll();
    public MessageResponseTo get(long id);
    public MessageResponseTo create(MessageRequestTo input);
    public MessageResponseTo update(MessageUpdateTo input);
    public void delete(long id);

}
