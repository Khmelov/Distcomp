package by.rest.publisher.service;

import by.rest.publisher.domain.Tag;
import by.rest.publisher.dto.TagRequestTo;
import by.rest.publisher.dto.TagResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.mapper.TagMapper;
import by.rest.publisher.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {
    
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    
    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }
    
    public TagResponseTo create(TagRequestTo request) {
        validateTagRequest(request);
        
        // Проверка уникальности имени тега
        Optional<Tag> existingTag = tagRepository.findByName(request.getName());
        if (existingTag.isPresent()) {
            throw new ApiException(400, "40020", "Tag with name '" + request.getName() + "' already exists");
        }
        
        Tag tag = tagMapper.toEntity(request);
        tag = tagRepository.save(tag);
        return tagMapper.toResponse(tag);
    }
    
    @Transactional(readOnly = true)
    public List<TagResponseTo> getAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public TagResponseTo getById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40402", "Tag not found with id: " + id));
        return tagMapper.toResponse(tag);
    }
    
    public TagResponseTo update(Long id, TagRequestTo request) {
        validateTagRequest(request);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40402", "Tag not found with id: " + id));
        
        // Проверка уникальности имени тега (кроме текущего тега)
        if (!tag.getName().equals(request.getName())) {
            Optional<Tag> existingTag = tagRepository.findByName(request.getName());
            if (existingTag.isPresent()) {
                throw new ApiException(400, "40020", "Tag with name '" + request.getName() + "' already exists");
            }
        }
        
        tag.setName(request.getName());
        tag = tagRepository.save(tag);
        return tagMapper.toResponse(tag);
    }
    
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ApiException(404, "40402", "Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<TagResponseTo> findByName(String name) {
        return tagRepository.findByName(name)
                .map(tagMapper::toResponse);
    }
    
    private void validateTagRequest(TagRequestTo request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ApiException(400, "40021", "Tag name cannot be empty");
        }
        if (request.getName().length() < 2 || request.getName().length() > 32) {
            throw new ApiException(400, "40022", "Tag name must be between 2 and 32 characters");
        }
    }
}