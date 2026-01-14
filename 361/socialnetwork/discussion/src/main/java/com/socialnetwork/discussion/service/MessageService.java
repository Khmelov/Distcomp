package com.socialnetwork.discussion.service;

import com.socialnetwork.discussion.dto.request.MessageRequestDto;
import com.socialnetwork.discussion.dto.response.MessageResponseDto;
import java.util.List;

public interface MessageService {
    List<MessageResponseDto> getAll();
    MessageResponseDto getById(Long id);
    MessageResponseDto create(MessageRequestDto request);
    MessageResponseDto update(Long id, MessageRequestDto request);
    void delete(Long id);
    List<MessageResponseDto> getByTweetId(Long tweetId);
    List<MessageResponseDto> getByCountryAndTweetId(String country, Long tweetId);
}