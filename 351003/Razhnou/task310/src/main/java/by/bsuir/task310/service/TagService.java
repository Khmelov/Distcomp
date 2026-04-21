package by.bsuir.task310.service;

import by.bsuir.task310.dto.request.TagRequestTo;
import by.bsuir.task310.dto.response.TagResponseTo;
import by.bsuir.task310.entity.Story;
import by.bsuir.task310.entity.Tag;
import by.bsuir.task310.exception.BadRequestException;
import by.bsuir.task310.exception.NotFoundException;
import by.bsuir.task310.mapper.TagMapper;
import by.bsuir.task310.repository.StoryRepository;
import by.bsuir.task310.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final StoryRepository storyRepository;

    public TagService(TagRepository tagRepository, StoryRepository storyRepository) {
        this.tagRepository = tagRepository;
        this.storyRepository = storyRepository;
    }

    public TagResponseTo create(TagRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Tag id must be null on create", 3);
        }
        validateText(request.name(), "Tag name");

        Tag tag = TagMapper.toEntity(request);
        return TagMapper.toResponse(tagRepository.save(tag));
    }

    public List<TagResponseTo> findAll() {
        return tagRepository.findAll().stream()
                .map(TagMapper::toResponse)
                .toList();
    }

    public TagResponseTo findById(Long id) {
        validateId(id, "Tag id");
        return TagMapper.toResponse(getTag(id));
    }

    public TagResponseTo update(TagRequestTo request) {
        validateId(request.id(), "Tag id");
        validateText(request.name(), "Tag name");
        getTag(request.id());

        Tag tag = TagMapper.toEntity(request);
        return TagMapper.toResponse(tagRepository.update(tag));
    }

    public void delete(Long id) {
        validateId(id, "Tag id");
        getTag(id);

        for (Story story : storyRepository.findAll()) {
            if (story.getTagIds().contains(id)) {
                List<Long> tagIds = story.getTagIds().stream()
                        .filter(tagId -> !tagId.equals(id))
                        .toList();
                story.setTagIds(tagIds);
                story.setModified(Instant.now().toString());
                storyRepository.update(story);
            }
        }
        tagRepository.deleteById(id);
    }

    public List<TagResponseTo> findByStoryId(Long storyId) {
        validateId(storyId, "Tweet id");
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Tweet not found", 2));

        return story.getTagIds().stream()
                .map(this::getTag)
                .map(TagMapper::toResponse)
                .toList();
    }

    private Tag getTag(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found", 3));
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank", 2);
        }
    }
}
