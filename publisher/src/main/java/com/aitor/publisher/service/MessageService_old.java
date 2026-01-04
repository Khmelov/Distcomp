package com.aitor.publisher.service;

import com.aitor.publisher.dto.MessageRequestTo;
import com.aitor.publisher.dto.MessageResponseTo;
import com.aitor.publisher.exception.EntityNotExistsException;
import com.aitor.publisher.model.Issue;
import com.aitor.publisher.model.Message;
import com.aitor.publisher.repository.IssueRepository;
import com.aitor.publisher.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService_old {
    private final MessageRepository repository;
    private final IssueRepository issueRepository;

    public MessageResponseTo add(MessageRequestTo requestBody){
        Message persisted = repository.save(new Message(
                getIssue(requestBody.getIssueId()),
                requestBody.getContent()));
        return toResponse(persisted);
    }

    public MessageResponseTo set(Long id, MessageRequestTo requestBody){
        var entity = getEntity(id);
        entity.setIssueId(getIssue(requestBody.getIssueId()));
        entity.setContent(requestBody.getContent());
        return toResponse(repository.save(entity));
    }

    public MessageResponseTo get(Long id) {
        return toResponse(getEntity(id));
    }

    public List<MessageResponseTo> getAll(){
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

    private Issue getIssue(Long id){
        var entity = issueRepository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private MessageResponseTo toResponse(Message entity){
        return new MessageResponseTo(
                entity.getId(),
                entity.getIssueId().getId(),
                entity.getContent());
    }
}
