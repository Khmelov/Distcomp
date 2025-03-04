package com.bsuir.dc.service;

import com.bsuir.dc.dto.request.PostRequestTo;
import com.bsuir.dc.dto.response.PostResponseTo;
import com.bsuir.dc.model.Post;
import com.bsuir.dc.model.Topic;
import com.bsuir.dc.repository.PostRepository;
import com.bsuir.dc.repository.TopicRepository;
import com.bsuir.dc.util.exception.EntityNotFoundException;
import com.bsuir.dc.util.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostService(PostRepository postRepository, TopicRepository topicRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
        this.postMapper = postMapper;
    }

    private void setArticle(Post post, long articleId){
        Topic topic = topicRepository.findById(articleId).orElseThrow(() -> new EntityNotFoundException("Topic with such id does not exist"));
        post.setTopic(topic);
    }

    public PostResponseTo save(PostRequestTo postRequestTo) {
        Post post = postMapper.toPost(postRequestTo);
        setArticle(post, postRequestTo.getTopicId());
        return postMapper.toPostResponse(postRepository.save(post));
    }

    public List<PostResponseTo> findAll() { return postMapper.toPostResponseList(postRepository.findAll()); }

    public PostResponseTo findById(long id) {
        return postMapper.toPostResponse(postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with such id does not exist")));
    }

    public void deleteById(long id) {
        if (!postRepository.existsById(id)) { throw new EntityNotFoundException("Post with such id does not exist"); }
        postRepository.deleteById(id);
    }

    public PostResponseTo update(PostRequestTo commentRequestDTO) {
        Post post = postMapper.toPost(commentRequestDTO);
        Long topicId = commentRequestDTO.getTopicId();
        if (topicId != null) { setArticle(post, topicId); }
        return postMapper.toPostResponse(postRepository.save(post));
    }
}
