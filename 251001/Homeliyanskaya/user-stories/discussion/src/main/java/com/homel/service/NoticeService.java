package com.homel.service;

import com.homel.dto.StoryResponseTo;
import com.homel.service.client.StoryClient;
import com.homel.dto.NoticeResponseTo;
import com.homel.dto.NoticeRequestTo;
import com.homel.exception.EntityNotFoundException;
import com.homel.mapper.NoticeMapper;
import com.homel.model.Notice;
import com.homel.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final StoryClient storyClient;  // Используем внешний клиент для работы с Story

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, StoryClient storyClient) {
        this.noticeRepository = noticeRepository;
        this.storyClient = storyClient;
    }

    public NoticeResponseTo createNotice(NoticeRequestTo noticeRequest) {
        // Создаем сущность Notice на основе данных из DTO
        Notice notice = NoticeMapper.INSTANCE.toEntity(noticeRequest);

        // Получаем Story через внешний сервис (API)
        // Это предполагает, что у тебя есть метод getStoryById в StoryClient
        StoryResponseTo story = storyClient.getStoryById(noticeRequest.getStoryId()); // предполагаем, что StoryClient возвращает DTO

        if (story == null) {
            throw new EntityNotFoundException("Story not found");
        }

        notice.setStoryId(NoticeMapper.convertLongToUUID(noticeRequest.getStoryId())); // Сохраняем только идентификатор

        Notice savedNotice = noticeRepository.save(notice);
        return NoticeMapper.INSTANCE.toResponse(savedNotice);
    }

    public NoticeResponseTo getNotice(Long id) {
        return noticeRepository.findById(NoticeMapper.convertLongToUUID(id))
                .map(NoticeMapper.INSTANCE::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Notice not found"));
    }

    public List<NoticeResponseTo> getAllNotices() {
        List<NoticeResponseTo> a = null;
        try {
            a =  noticeRepository.findAll().stream()
                    .map(NoticeMapper.INSTANCE::toResponse)
                    .toList();
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        return a;
    }

    public void deleteNotice(Long id) {
        noticeRepository.findById(NoticeMapper.convertLongToUUID(id))
                .orElseThrow(() -> new EntityNotFoundException("Notice with id " + id + " not found"));

        noticeRepository.deleteById(NoticeMapper.convertLongToUUID(id));
    }

    public NoticeResponseTo updateNotice(NoticeRequestTo noticeRequest) {
        Notice existingNotice = noticeRepository.findById(NoticeMapper.convertLongToUUID(noticeRequest.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Notice not found"));

        // Получаем Story через внешний сервис
        StoryResponseTo story = storyClient.getStoryById(noticeRequest.getStoryId());

        if (story == null) {
            throw new EntityNotFoundException("Story not found");
        }

        existingNotice.setStoryId(NoticeMapper.convertLongToUUID(noticeRequest.getStoryId())); // Обновляем только идентификатор
        existingNotice.setContent(noticeRequest.getContent());

        Notice updatedNotice = noticeRepository.save(existingNotice);

        return NoticeMapper.INSTANCE.toResponse(updatedNotice);
    }
}

