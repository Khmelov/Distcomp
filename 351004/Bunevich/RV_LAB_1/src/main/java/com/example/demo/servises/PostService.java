package com.example.demo.servises;

import com.example.demo.dto.request.PostRequestTo;
import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.PostResponseTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.exeptionHandler.WriterNotFoundException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.memoryRepository.repozitoryImplementation.PostInMemoryRepository;
import com.example.demo.models.Post;
import com.example.demo.models.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostInMemoryRepository postInMemoryRepository;

    public List<PostResponseTo> getPosts(){
        return postMapper.toEntityList(postInMemoryRepository.findAll());
    }
    public PostResponseTo create(PostRequestTo postRequestTo){
        Post savedPost = postInMemoryRepository.save(postMapper.toEntity(postRequestTo));
        return postMapper.toResponse(savedPost);
    }
    public void delete(Long id){
        if(!postInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        postInMemoryRepository.deleteById(id);
    }
    public PostResponseTo findById(Long id){
        if(!postInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        Post post = postInMemoryRepository.findById(id).orElseThrow(() -> new WriterNotFoundException(id));
        return postMapper.toResponse(post);
    }
    public PostResponseTo update(Long id, PostRequestTo postRequestTo){
        if(!postInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        return postMapper.toResponse(postInMemoryRepository.update(id, postMapper.toEntity(postRequestTo)));
    }

}
