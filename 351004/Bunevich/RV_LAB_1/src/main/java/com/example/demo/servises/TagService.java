package com.example.demo.servises;

import com.example.demo.dto.request.TagRequestTo;
import com.example.demo.dto.response.TagResponseTo;
import com.example.demo.exeptionHandler.WriterNotFoundException;
import com.example.demo.mapper.TagMapper;
import com.example.demo.memoryRepository.repozitoryImplementation.TagInMemoryRepository;
import com.example.demo.models.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagMapper tagMapper;
    private final TagInMemoryRepository tagInMemoryRepository;
    public List<TagResponseTo> getTag(){
        return tagMapper.entityListToResponse(tagInMemoryRepository.findAll());
    }
    public TagResponseTo create(TagRequestTo request){
        Tag newTag = tagInMemoryRepository.save(tagMapper.toEntity(request));
        return tagMapper.toResponse(newTag);
    }
    public void deleteTag(Long id){
        if(!tagInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        tagInMemoryRepository.deleteById(id);
    }
    public TagResponseTo findTagById(Long id){
        if(!tagInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        Tag tag = tagInMemoryRepository.findById(id).orElseThrow(() -> new WriterNotFoundException(id));
        return tagMapper.toResponse(tag);
    }
    public TagResponseTo uptadeTag(Long id, TagRequestTo tagRequest){
        if(!tagInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        Tag tag = tagInMemoryRepository.update(id, tagMapper.toEntity(tagRequest));
        return tagMapper.toResponse(tag);
    }
}
