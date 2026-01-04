package com.blog.service;

import com.blog.client.DiscussionClient;
import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseFromDiscussion;
import com.blog.dto.response.MessageResponseTo;
import com.blog.model.Message;
import com.blog.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService implements MessageServiceInterface {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private DiscussionClient discussionClient;

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseTo> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponseTo getMessageById(Long id) {
        Optional<Message> localMessage = messageRepository.findById(id);

        if (localMessage.isPresent()) {
            Message message = localMessage.get();
            if ("PENDING".equals(message.getState())) {
                try {
                    MessageResponseTo synced = discussionClient.getMessage(id);
                    if (synced != null && !"PENDING".equals(synced.getState())) {
                        updateFromResponse(message, synced);
                        messageRepository.save(message);
                        return toResponse(message);
                    }
                } catch (Exception e) {
                    System.err.println("Sync failed for message " + id + ": " + e.getMessage());
                }
            }
            return toResponse(message);
        } else {
            try {
                MessageResponseTo fromDiscussion = discussionClient.getMessage(id);
                if (fromDiscussion != null && !"NOT_FOUND".equals(fromDiscussion.getState())) {
                    Message newMessage = createMessageFromResponse(fromDiscussion);
                    messageRepository.save(newMessage);
                    return fromDiscussion;
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch from discussion for id " + id + ": " + e.getMessage());
            }

            return createNotFoundResponse(id);
        }
    }

    @Override
    @Transactional
    public MessageResponseTo createMessage(MessageRequestTo request) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        Long tempId = generateMessageId();

        Message message = new Message();
        message.setId(tempId);
        message.setTopicId(request.getTopicId());
        message.setContent(request.getContent());
        message.setEditorId(currentUserId);
        message.setCountry(request.getCountry() != null ? request.getCountry() : "global");
        message.setState("PENDING");
        message.setCreated(LocalDateTime.now());
        message.setModified(LocalDateTime.now());

        message = messageRepository.save(message);

        request.setId(tempId);
        request.setEditorId(currentUserId);
        request.setState("PENDING");

        return toResponse(message);
    }

    @Override
    @Transactional
    public MessageResponseTo updateMessage(Long id, MessageRequestTo request) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        Optional<Message> existing = messageRepository.findById(id);

        if (existing.isEmpty()) {
            throw new RuntimeException("Message not found with id: " + id);
        }

        Message message = existing.get();

        // Проверка владельца
        if (!message.getEditorId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to update this message");
        }

        // Синхронизация статуса
        try {
            MessageResponseTo fromDiscussion = discussionClient.getMessage(id);
            if (fromDiscussion != null && !"NOT_FOUND".equals(fromDiscussion.getState())) {
                message.setState(fromDiscussion.getState());
            }
        } catch (Exception e) {
            System.err.println("Sync failed for message " + id + ": " + e.getMessage());
        }

        message.setContent(request.getContent());
        message.setState("PENDING");
        message.setModified(LocalDateTime.now());

        message = messageRepository.save(message);

        request.setId(id);
        request.setEditorId(currentUserId);
        request.setState("PENDING");

        return toResponse(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            if (!message.getEditorId().equals(currentUserId)) {
                throw new RuntimeException("You are not authorized to delete this message");
            }

            message.setState("DELETED");
            message.setModified(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsMessage(Long id) {
        Optional<Message> message = messageRepository.findById(id);
        return message.isPresent() && !"DELETED".equals(message.get().getState());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseTo> getMessagesByEditorId(Long editorId) {
        return messageRepository.findByEditorId(editorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponseTo> getMessagesByEditorId(Long editorId, Pageable pageable) {
        return messageRepository.findByEditorId(editorId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMessageOwner(Long messageId, Long editorId) {
        return messageRepository.existsByIdAndEditorId(messageId, editorId);
    }

    @Override
    @Transactional
    public MessageResponseTo syncMessageFromDiscussion(MessageResponseFromDiscussion response) {
        if (response.getId() == null) {
            throw new RuntimeException("Cannot sync message without ID");
        }

        Optional<Message> existingOpt = messageRepository.findById(response.getId());

        if (existingOpt.isPresent()) {
            Message message = existingOpt.get();
            updateFromDiscussionResponse(message, response);
            message = messageRepository.save(message);
            return toResponse(message);
        } else {
            Message newMessage = createMessageFromDiscussionResponse(response);
            newMessage = messageRepository.save(newMessage);
            return toResponse(newMessage);
        }
    }

    // Вспомогательные методы
    private Long generateMessageId() {
        return System.currentTimeMillis();
    }

    private void updateFromDiscussionResponse(Message message, MessageResponseFromDiscussion response) {
        if (response.getTopicId() != null) {
            message.setTopicId(response.getTopicId());
        }
        if (response.getContent() != null) {
            message.setContent(response.getContent());
        }
        if (response.getEditorId() != null) {
            message.setEditorId(response.getEditorId());
        }
        if (response.getCountry() != null) {
            message.setCountry(response.getCountry());
        }
        message.setState(response.getNormalizedState());
        if (response.getModified() != null) {
            message.setModified(response.getModified());
        } else {
            message.setModified(LocalDateTime.now());
        }
    }

    private void updateFromResponse(Message message, MessageResponseTo response) {
        if (response.getTopicId() != null) {
            message.setTopicId(response.getTopicId());
        }
        if (response.getContent() != null) {
            message.setContent(response.getContent());
        }
        if (response.getEditorId() != null) {
            message.setEditorId(response.getEditorId());
        }
        if (response.getCountry() != null) {
            message.setCountry(response.getCountry());
        }
        message.setState(response.getState());
        message.setModified(LocalDateTime.now());
    }

    private Message createMessageFromResponse(MessageResponseTo response) {
        Message message = new Message();
        message.setId(response.getId());
        message.setTopicId(response.getTopicId());
        message.setContent(response.getContent());
        message.setEditorId(response.getEditorId());
        message.setCountry(response.getCountry());
        message.setState(response.getState() != null ? response.getState() : "PENDING");
        message.setCreated(response.getCreated() != null ? response.getCreated() : LocalDateTime.now());
        message.setModified(LocalDateTime.now());
        return message;
    }

    private Message createMessageFromDiscussionResponse(MessageResponseFromDiscussion response) {
        Message message = new Message();
        message.setId(response.getId());
        message.setTopicId(response.getTopicId());
        message.setContent(response.getContent());
        message.setEditorId(response.getEditorId());
        message.setCountry(response.getCountry());
        message.setState(response.getNormalizedState());
        message.setCreated(response.getCreated() != null ? response.getCreated() : LocalDateTime.now());
        message.setModified(LocalDateTime.now());
        return message;
    }

    private MessageResponseTo toResponse(Message message) {
        MessageResponseTo response = new MessageResponseTo();
        response.setId(message.getId());
        response.setTopicId(message.getTopicId());
        response.setContent(message.getContent());
        response.setEditorId(message.getEditorId());
        response.setCountry(message.getCountry());
        response.setState(message.getState());
        response.setCreated(message.getCreated());
        response.setModified(message.getModified());

        if (message.getState() != null) {
            response.setApproved("APPROVED".equals(message.getState()) || "APPROVE".equals(message.getState()));
            response.setDeclined("DECLINED".equals(message.getState()) || "DECLINE".equals(message.getState()));
            response.setPending("PENDING".equals(message.getState()));
        }

        return response;
    }

    private MessageResponseTo createNotFoundResponse(Long id) {
        MessageResponseTo notFoundResponse = new MessageResponseTo();
        notFoundResponse.setId(id);
        notFoundResponse.setState("NOT_FOUND");
        notFoundResponse.setContent("Message not found");
        notFoundResponse.setApproved(false);
        notFoundResponse.setDeclined(false);
        notFoundResponse.setPending(false);
        return notFoundResponse;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }
}