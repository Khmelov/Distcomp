package com.example.demo.servises;

import com.example.demo.dto.request.TagRequestTo;
import com.example.demo.dto.response.TagResponseTo;
import com.example.demo.exeptionHandler.ResourceNotFoundException;
import com.example.demo.mapper.TagMapper;
import com.example.demo.models.Tag;
import com.example.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagMapper tagMapper;
    private final TagRepository tagRepository;

    public TagService(TagMapper tagMapper, TagRepository tagRepository) {
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
    }

    public List<TagResponseTo> getTag(int page, int size, String sortBy, String sortDir, String name){
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Specification<Tag> spec = (root, query, cb) ->
                name == null || name.isBlank() ? cb.conjunction() : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        return tagMapper.entityListToResponse(tagRepository.findAll(spec, PageRequest.of(page, size, sort)).getContent());
    }

    public TagResponseTo create(TagRequestTo request){
        Tag newTag = tagRepository.save(tagMapper.toEntity(request));
        return tagMapper.toResponse(newTag);
    }

    public void deleteTag(Long id){
        if(!tagRepository.existsById(id)){
            throw new ResourceNotFoundException("Tag", id);
        }
        tagRepository.deleteById(id);
    }

    public TagResponseTo findTagById(Long id){
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        return tagMapper.toResponse(tag);
    }

    public TagResponseTo uptadeTag(Long id, TagRequestTo tagRequest){
        Tag existingTag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        Tag tag = tagMapper.toEntity(tagRequest);
        tag.setId(existingTag.getId());
        tag = tagRepository.save(tag);
        return tagMapper.toResponse(tag);
    }
}
