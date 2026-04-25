package com.example.demo.servises;

import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.exeptionHandler.WriterNotFoundException;
import com.example.demo.mapper.StoryMapper;
import com.example.demo.memoryRepository.repozitoryImplementation.StoryInMemoryRepository;
import com.example.demo.models.Story;
import com.example.demo.models.Writer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoryService {
    private final StoryMapper storyMapper;
    private final StoryInMemoryRepository storyInMemoryRepository;
    public List<StoryResponseTo> findAll(){
         return storyMapper.storyListToResponseList(storyInMemoryRepository.findAll());
    }
    public StoryResponseTo create(StoryRequestTo storyRequestTo){
        Story savedStory = storyInMemoryRepository.save(storyMapper.toEntity(storyRequestTo));
        return storyMapper.toResponse(savedStory);
    }
    public void delete(Long id){
        if(!storyInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        storyInMemoryRepository.deleteById(id);
    }
    public StoryResponseTo findById(Long id){
        if(!storyInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        Story story = storyInMemoryRepository.findById(id).orElseThrow(() -> new WriterNotFoundException(id));
        return storyMapper.toResponse(story);
    }
    public StoryResponseTo update(Long id, StoryRequestTo storyRequestTo){
        if(!storyInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        return storyMapper.toResponse(storyInMemoryRepository.update(id, storyMapper.toEntity(storyRequestTo)));
    }

}
