package com.group310971.gormash.service;

import com.group310971.gormash.dto.MessageRequestTo;
import com.group310971.gormash.dto.MessageResponseTo;
import com.group310971.gormash.mapper.MessageMapper;
import com.group310971.gormash.model.Message;
import com.group310971.gormash.model.Topic;
import com.group310971.gormash.repository.MessageRepository;
import com.group310971.gormash.repository.TopicRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final TopicRepository topicRepository;
    private final MessageMapper messageMapper = MessageMapper.INSTANCE;

    public MessageResponseTo createMessage(@Valid MessageRequestTo messageRequestTo){
        Message message = messageMapper.toEntity(messageRequestTo);
        Optional<Topic> optional = topicRepository.findById(messageRequestTo.getTopicId());
        if (optional.isEmpty())
            throw new RuntimeException("Message not exists");
        message.setTopic(optional.get());
        Message savedMessage = messageRepository.save(message);
        return messageMapper.toResponse(savedMessage);
    }

    public MessageResponseTo updateMessage(Long id, @Valid MessageRequestTo messageRequestTo){
        if (id == null)
            id = messageRequestTo.getId();
        else
            messageRequestTo.setId(id);
        if (id == null) {
            throw new RuntimeException("Message id cannot be null for update");
        }
        var optional = messageRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Message not exists");
        Optional<Topic> editorOptional = topicRepository.findById(messageRequestTo.getTopicId());
        if (editorOptional.isEmpty())
            throw new RuntimeException("Message not exists");
        Message persistence = optional.get();
        Message message = messageMapper.toEntity(messageRequestTo);
        persistence.setContent(message.getContent());
        persistence.setTopic(editorOptional.get());
        Message updatedMessage = messageRepository.save(persistence);
        return messageMapper.toResponse(updatedMessage);
    }

    public MessageResponseTo getMessageById(Long id) {
        return messageRepository.findById(id)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
    }

    public List<MessageResponseTo> getAllMessages(){
        LinkedList<MessageResponseTo> list = new LinkedList<>();
        for (Message message : messageRepository.findAll()){
            list.add(messageMapper.toResponse(message));
        }
        return list;
    }

    public MessageResponseTo deleteMessage(Long id) {
        var optional = messageRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Message not exists");
        Message message = optional.get();
        messageRepository.delete(message);
        return messageMapper.toResponse(message);
    }
}
