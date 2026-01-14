package org.example;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
@Transactional
public class StoryService {

    private final StoryRepository storyRepository;
    private final WriterRepository writerRepository;
    private final TagRepository tagRepository;
    private final StoryMapper storyMapper;

    public StoryService(
            StoryRepository storyRepository,
            WriterRepository writerRepository,
            TagRepository tagRepository,
            StoryMapper storyMapper) {
        this.storyRepository = storyRepository;
        this.writerRepository = writerRepository;
        this.tagRepository = tagRepository;
        this.storyMapper = storyMapper;
    }

    // ---------- CREATE ----------
    @Transactional
    public StoryResponseTo create(StoryRequestTo dto) {

        Story story = storyMapper.toEntity(dto);

        Writer writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Writer not found"));

        story.setWriter(writer);

        // 1. Сохраняем story → получаем id
        storyRepository.saveAndFlush(story);

        // 2. Создаём теги и StoryTag
        if (dto.getStoryTags() != null) {
            for (String name : dto.getStoryTags()) {

                Tag tag = tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.saveAndFlush(new Tag(name)));

                story.addTag(tag);
            }
        }

        // 3. Сохраняем story ещё раз → каскад сохранит StoryTag
        return storyMapper.toResponse(storyRepository.saveAndFlush(story));
    }


    // ---------- READ ----------
    public StoryResponseTo getById(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));
        return storyMapper.toResponse(story);
    }

    public List<StoryResponseTo> getAll() {
        return storyRepository.findAll()
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    // ---------- UPDATE ----------
    public StoryResponseTo update(Long id, StoryRequestTo dto) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        story.setTitle(dto.getTitle());
        story.setContent(dto.getContent());

        return storyMapper.toResponse(story);
    }
    public Optional<StoryResponseTo> findById(Long id) {
        return storyRepository.findById(id)
                .map(storyMapper::toResponse);
    }

    // ---------- DELETE ----------
    @Transactional
    public void delete(Long id) {

        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        for (StoryTag st : new HashSet<>(story.getStoryTags())) {
            Tag tag = st.getTag();
            tag.getStoryTags().remove(st);

            if (tag.getStoryTags().isEmpty()) {
                tagRepository.delete(tag);
            }
        }

        storyRepository.delete(story);
    }

}