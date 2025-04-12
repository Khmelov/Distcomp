package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Message;
import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.MessageListMapper;
import com.bsuir.romanmuhtasarov.domain.mapper.MessageMapper;
import com.bsuir.romanmuhtasarov.domain.request.MessageRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.MessageResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchMessageException;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchNewsException;
import com.bsuir.romanmuhtasarov.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.serivces.MessageService;
import com.bsuir.romanmuhtasarov.serivces.NewsService;

import java.util.List;

@Service
@Transactional
@Validated
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final NewsService newsService;
    private final MessageMapper messageMapper;
    private final MessageListMapper messageListMapper;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, NewsService newsService, MessageMapper messageMapper, MessageListMapper messageListMapper) {
        this.messageRepository = messageRepository;
        this.newsService = newsService;
        this.messageMapper = messageMapper;
        this.messageListMapper = messageListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public MessageResponseTo create(@Valid MessageRequestTo entity) {
        News news = newsService.findNewsByIdExt(entity.newsId()).orElseThrow(() -> new NoSuchNewsException(entity.newsId()));
        Message message = messageMapper.toMessage(entity);
        message.setNews(news);
        return messageMapper.toMessageResponseTo(messageRepository.save(message));
    }

    @Override
    public List<MessageResponseTo> read() {
        return messageListMapper.toMessageResponseToList(messageRepository.findAll());
    }

    @Override
    @Validated(ValidationMarker.OnUpdate.class)
    public MessageResponseTo update(@Valid MessageRequestTo entity) {
        if (messageRepository.existsById(entity.id())) {
            Message message = messageMapper.toMessage(entity);
            News tweetRef = newsService.findNewsByIdExt(message.getNews().getId()).orElseThrow(() -> new NoSuchNewsException(message.getNews().getId()));
            message.setNews(tweetRef);
            return messageMapper.toMessageResponseTo(messageRepository.save(message));
        } else {
            throw new NoSuchMessageException(entity.id());
        }
    }

    @Override
    public void delete(Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
        } else {
            throw new NoSuchMessageException(id);
        }
    }

    @Override
    public MessageResponseTo findMessageById(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new NoSuchMessageException(id));
        return messageMapper.toMessageResponseTo(message);
    }
}
