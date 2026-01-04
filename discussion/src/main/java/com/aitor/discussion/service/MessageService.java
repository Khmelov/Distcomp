package com.aitor.discussion.service;

import com.aitor.publisher.dto.MessageRequestTo;
import com.aitor.publisher.dto.MessageResponseTo;
import com.aitor.discussion.model.Message;
import com.aitor.discussion.repository.MessageRepository;
import com.aitor.publisher.exception.EntityNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;

    public MessageResponseTo add(MessageRequestTo requestBody) {
        var message = new Message(requestBody.getIssueId(), requestBody.getContent());
        message.setStatus(message.getContent().toLowerCase().contains("panzerkampf") ?
                Message.Status.DELCINE :
                Message.Status.APPROVE);
        Message persisted = repository.save(message);
        return toResponse(persisted);
    }

    public MessageResponseTo set(Long id, MessageRequestTo requestBody){
        var entity = getEntity(id);
        entity.setIssueId(requestBody.getIssueId());
        entity.setContent(requestBody.getContent());
        return toResponse(repository.save(entity));
    }

    public MessageResponseTo get(Long id) {
        return toResponse(getEntity(id));
    }

    public List<MessageResponseTo> getAll() {
        return repository.findAll().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
    }

    public MessageResponseTo remove(Long id) {
        var entityOptional = repository.findById(id);
        if (entityOptional.isPresent()) {
            var entity = entityOptional.get();
            var response = toResponse(entity);
            repository.delete(entity);
            return response;
        } else
            throw new EntityNotExistsException();
    }

    private Message getEntity(Long id){
        var entity = repository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private MessageResponseTo toResponse(Message entity){
        return new MessageResponseTo(
                entity.getId(),
                entity.getIssueId(),
                entity.getContent());
    }
}
