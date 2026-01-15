package com.example.task310rest.service;

import com.example.task310rest.dto.request.MarkRequestTo;
import com.example.task310rest.dto.response.MarkResponseTo;
import com.example.task310rest.entity.Mark;
import com.example.task310rest.exception.ResourceNotFoundException;
import com.example.task310rest.mapper.MarkMapper;
import com.example.task310rest.repository.MarkRepository;
import com.example.task310rest.repository.TweetMarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с Mark
 */
@Service
@RequiredArgsConstructor
public class MarkService {
    
    private final MarkRepository markRepository;
    private final TweetMarkRepository tweetMarkRepository;
    private final MarkMapper markMapper;
    
    /**
     * Создать новую метку
     */
    public MarkResponseTo create(MarkRequestTo requestTo) {
        Mark mark = markMapper.toEntity(requestTo);
        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponseTo(savedMark);
    }
    
    /**
     * Получить метку по ID
     */
    public MarkResponseTo getById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        return markMapper.toResponseTo(mark);
    }
    
    /**
     * Получить метку по имени
     */
    public MarkResponseTo getByName(String name) {
        Mark mark = markRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Mark with name='" + name + "' not found"));
        return markMapper.toResponseTo(mark);
    }
    
    /**
     * Получить все метки
     */
    public List<MarkResponseTo> getAll() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponseTo)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновить метку
     */
    public MarkResponseTo update(Long id, MarkRequestTo requestTo) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        
        existingMark.setName(requestTo.getName());
        
        Mark updatedMark = markRepository.update(existingMark);
        return markMapper.toResponseTo(updatedMark);
    }
    
    /**
     * Частичное обновление метки (PATCH)
     */
    public MarkResponseTo partialUpdate(Long id, MarkRequestTo requestTo) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        
        markMapper.updateEntityFromRequestTo(requestTo, existingMark);
        Mark updatedMark = markRepository.update(existingMark);
        return markMapper.toResponseTo(updatedMark);
    }
    
    /**
     * Удалить метку
     */
    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark", id);
        }
        
        // Удаляем связи с твитами
        tweetMarkRepository.removeAllByMarkId(id);
        
        markRepository.deleteById(id);
    }
}
