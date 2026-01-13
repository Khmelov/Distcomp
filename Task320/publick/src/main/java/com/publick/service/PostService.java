package com.publick.service;

import com.publick.dto.PostRequestTo;
import com.publick.dto.PostResponseTo;
import com.publick.entity.Issue;
import com.publick.entity.Post;
import com.publick.repository.IssueRepository;
import com.publick.repository.PostRepository;
import com.publick.service.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private PostMapper postMapper;

    public PostResponseTo create(PostRequestTo request) {
        // Validate that issue exists
        Issue issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Issue not found with id: " + request.getIssueId()));

        Post post = postMapper.toEntity(request, issue);
        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public PostResponseTo getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Post not found with id: " + id));
        return postMapper.toResponse(post);
    }

    public List<PostResponseTo> getAll() {
        return postRepository.findAll().stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<PostResponseTo> getAll(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(postMapper::toResponse);
    }

    public PostResponseTo update(Long id, PostRequestTo request) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Post not found with id: " + id));

        // Validate that issue exists
        Issue issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Issue not found with id: " + request.getIssueId()));

        postMapper.updateEntityFromDto(request, existing, issue);
        Post saved = postRepository.save(existing);
        return postMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new com.publick.exception.NotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
}