package com.example.discussion.service;

import com.example.discussion.dto.TopicRequestTo;
import com.example.discussion.dto.TopicResponseTo;
import com.example.discussion.model.Sticker;
import com.example.discussion.model.Topic;
import com.example.discussion.model.User;
import com.example.discussion.repository.StickerRepository;
import com.example.discussion.repository.TopicRepository;
import com.example.discussion.repository.UserRepository;
import com.example.discussion.service.mapper.TopicMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private  final UserRepository userRepository;
    private  final StickerRepository stickerRepository;
    private final TopicMapper topicMapper = Mappers.getMapper(TopicMapper.class);

    public TopicService(TopicRepository topicRepository, UserRepository userRepository, StickerRepository stickerRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.stickerRepository = stickerRepository;
    }

    public List<TopicResponseTo> findAll() {
        return topicRepository.findAll().stream()
                .map(topicMapper::toDto)
                .collect(Collectors.toList());
    }

    public TopicResponseTo findById(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        return topic.map(topicMapper::toDto).orElse(null);
    }
    @Transactional
    public TopicResponseTo save(TopicRequestTo topicRequestTo) {
        if (topicRepository.existsByTitle(topicRequestTo.getTitle())) {
            throw new EntityExistsException("A topic with the same title already exists.");
        }
        Long userId = topicRequestTo.getUserId();
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new EntityNotFoundException("Author not found with id " + userId);
        }
        Topic topic = topicMapper.toEntity(topicRequestTo);
        String[] stickers = topicRequestTo.getStickers();
        if(stickers.length != 0){
            for(String stikerName : stickers){
                Optional<Sticker> sticker = stickerRepository.findByName(stikerName);
                if(topic.getStickers() == null){
                    topic.setStickers(new HashSet<>());
                }
                if(sticker.isPresent()){
                    topic.getStickers().add(sticker.get());
                }
                else{
                    Sticker newSticker = new Sticker();
                    newSticker.setName(stikerName);
                    Sticker savedSticker = stickerRepository.save(newSticker);
                    topic.getStickers().add(savedSticker);
                }
            }
        }
        topic.setUser(user.get());
        LocalDateTime currentDate = LocalDateTime.now();
        topic.setCreated(currentDate);
        topic.setModified(currentDate);
        Topic savedTopic = topicRepository.save(topic);
        System.out.println(savedTopic.toString());
        return topicMapper.toDto(savedTopic);
    }


    public TopicResponseTo update(TopicRequestTo topicRequestTo) {
        Topic existingTopic = topicRepository.findById(topicRequestTo.getId()).orElseThrow(() -> new RuntimeException("Topic not found"));
        topicMapper.updateEntityFromDto(topicRequestTo, existingTopic);
        Topic updatedTopic = topicRepository.save(existingTopic);
        return topicMapper.toDto(updatedTopic);
    }

    public void deleteById(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new EntityNotFoundException("Topic not found with id " + id);
        }
        topicRepository.deleteById(id);
    }
}