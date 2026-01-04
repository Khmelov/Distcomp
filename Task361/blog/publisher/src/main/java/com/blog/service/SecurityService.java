package com.blog.service;

import com.blog.model.Editor;
import com.blog.model.Topic;
import com.blog.repository.EditorRepository;
import com.blog.repository.MessageRepository;
import com.blog.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EditorRepository editorRepository;

    public boolean isTopicOwner(Long topicId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<Topic> topicOpt = topicRepository.findById(topicId);
        if (topicOpt.isEmpty()) {
            return false;
        }

        Topic topic = topicOpt.get();
        Long editorId = topic.getEditor().getId();

        // Получаем ID текущего пользователя из аутентификации
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId().equals(editorId);
        }

        return false;
    }

    public boolean isMessageOwner(Long messageId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Optional<com.blog.model.Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return false;
        }

        com.blog.model.Message message = messageOpt.get();
        Long editorId = message.getEditorId();

        // Получаем ID текущего пользователя из аутентификации
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return editorId != null && userDetails.getId().equals(editorId);
        }

        return false;
    }

    public boolean isEditorOwner(Long editorId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Получаем ID текущего пользователя из аутентификации
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId().equals(editorId);
        }

        return false;
    }
}