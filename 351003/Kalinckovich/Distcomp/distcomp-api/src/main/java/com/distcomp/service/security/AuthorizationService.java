package com.distcomp.service.security;

import com.distcomp.dto.user.UserResponseDto;
import com.distcomp.service.note.NoteProxyService;
import com.distcomp.service.topic.TopicService;
import com.distcomp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;
    private final TopicService topicService;
    private final NoteProxyService noteProxyService;

    public Mono<Boolean> isUserSelf(final Long userId, final String login) {
        return userService.findById(userId)
                .map(UserResponseDto::getLogin)
                .map(login::equals)
                .defaultIfEmpty(false);
    }


    public Mono<Boolean> isTopicOwner(final Long topicId, final String login) {
        return topicService.findById(topicId)
                .flatMap(topic -> userService.findById(topic.getUserId()))
                .map(UserResponseDto::getLogin)
                .map(login::equals)
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> isNoteOwner(final Long noteId, final String login) {
        return noteProxyService.getNoteById(noteId)
                .flatMap(note -> topicService.findById(note.getTopicId()))
                .flatMap(topic -> userService.findById(topic.getUserId()))
                .map(UserResponseDto::getLogin)
                .map(login::equals)
                .defaultIfEmpty(false);
    }
}