package com.example.discussion.service;
import com.example.discussion.dto.MessageRequestTo;
import com.example.discussion.dto.MessageResponseTo;
import com.example.discussion.exception.ServiceException;
import com.example.discussion.mapper.MessageMapper;
import com.example.discussion.repository.MessageRepository;
import com.example.discussion.model.Message;
import com.example.discussion.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageResponseTo createMessage(MessageRequestTo request) {
        validateMessageRequest(request);
        Message entity = messageMapper.toEntity(request);
        entity.setIssueId(request.getIssueId());
        try{
            return messageMapper.toResponse(messageRepository.save(entity));
        }
        catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public List<MessageResponseTo> getAllMessages() {
        return messageMapper.listToResponse(messageRepository.findAll());
    }

    public MessageResponseTo getMessageById(Long id) {
        Message message = messageRepository.findByCountryAndId("bl",id)
                .orElseThrow(() -> new ServiceException("Message not found with id: " + id, 404));
        return messageMapper.toResponse(message);
    }

    public MessageResponseTo updateMessage(MessageRequestTo request) {
        validateMessageRequest(request);
        Message entity = messageMapper.toEntity(request);
        if (!messageRepository.existsByCountryAndId("bl",entity.getId())) {
            throw new ServiceException("Message not found with id: " + entity.getId(), 404);
        }
        entity.setIssueId(request.getIssueId());
        try{
            return messageMapper.toResponse(messageRepository.save(entity));
        }
        catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public void deleteMessage(Long id) {
        if (!messageRepository.existsByCountryAndId("bl",id)) {
            throw new ServiceException("Message not found with id: " + id, 404);
        }
        messageRepository.deleteByCountryAndId("bl",id);
    }

    private void validateMessageRequest(MessageRequestTo request) {
        ValidationUtils.validateNotNull(request, "Message");
        ValidationUtils.validateString(request.getContent(), "Text", 2, 2048);
        ValidationUtils.validateNotNegative(request.getIssueId(), "Issue ID");
    }
}
