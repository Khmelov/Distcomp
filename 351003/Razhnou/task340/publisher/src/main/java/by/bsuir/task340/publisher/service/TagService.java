package by.bsuir.task340.publisher.service;

import by.bsuir.task340.publisher.dto.request.TagRequestTo;
import by.bsuir.task340.publisher.dto.response.TagResponseTo;
import by.bsuir.task340.publisher.entity.Tag;
import by.bsuir.task340.publisher.exception.BadRequestException;
import by.bsuir.task340.publisher.exception.NotFoundException;
import by.bsuir.task340.publisher.mapper.TagMapper;
import by.bsuir.task340.publisher.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public TagResponseTo create(TagRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Tag id must be null on create", 3);
        }
        validateName(request.name());

        Tag tag = TagMapper.toEntity(request);
        return TagMapper.toResponse(tagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public List<TagResponseTo> findAll() {
        return tagRepository.findAll().stream()
                .map(TagMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TagResponseTo findById(Long id) {
        validateId(id, "Tag id");
        return TagMapper.toResponse(getTag(id));
    }

    @Transactional
    public TagResponseTo update(TagRequestTo request) {
        validateId(request.id(), "Tag id");
        validateName(request.name());

        Tag tag = getTag(request.id());
        TagMapper.updateEntity(tag, request);
        return TagMapper.toResponse(tagRepository.save(tag));
    }

    @Transactional
    public void delete(Long id) {
        validateId(id, "Tag id");
        tagRepository.delete(getTag(id));
    }

    @Transactional(readOnly = true)
    public Set<Tag> getTagsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        for (Long id : ids) {
            validateId(id, "Tag id");
        }
        Set<Long> uniqueIds = new LinkedHashSet<>(ids);
        Set<Tag> tags = new LinkedHashSet<>(tagRepository.findAllById(uniqueIds));
        if (tags.size() != uniqueIds.size()) {
            throw new BadRequestException("Some tags do not exist", 4);
        }
        return tags;
    }

    @Transactional
    public Set<Tag> resolveTags(List<Long> tagIds, List<String> tagNames) {
        Set<Tag> result = new LinkedHashSet<>(getTagsByIds(tagIds));
        if (tagNames == null || tagNames.isEmpty()) {
            return result;
        }

        Map<String, String> normalizedNames = new LinkedHashMap<>();
        for (String tagName : tagNames) {
            validateName(tagName);
            normalizedNames.putIfAbsent(tagName.trim(), tagName.trim());
        }

        List<String> names = new ArrayList<>(normalizedNames.values());
        Map<String, Tag> existingByName = new LinkedHashMap<>();
        for (Tag tag : tagRepository.findByNameIn(names)) {
            existingByName.put(tag.getName(), tag);
        }

        for (String name : names) {
            Tag tag = existingByName.get(name);
            if (tag == null) {
                tag = tagRepository.save(new Tag(null, name));
            }
            result.add(tag);
        }
        return result;
    }

    public Tag getTag(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found", 2));
    }

    @Transactional
    public void cleanupUnusedTags(Set<Tag> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return;
        }
        for (Tag tag : candidates) {
            if (tag.getId() != null && tagRepository.countTweetsUsingTag(tag.getId()) == 0) {
                tagRepository.delete(tag);
            }
        }
        tagRepository.flush();
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Tag name must not be blank", 2);
        }
        int length = name.trim().length();
        if (length < 2 || length > 32) {
            throw new BadRequestException("Tag name length must be between 2 and 32", 5);
        }
    }
}
