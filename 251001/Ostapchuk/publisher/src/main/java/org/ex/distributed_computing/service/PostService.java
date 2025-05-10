package org.ex.distributed_computing.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.config.DiscussionCommunicationProps;
import org.ex.distributed_computing.dto.request.PostRequestDTO;
import org.ex.distributed_computing.dto.response.PostResponseDTO;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.repository.PostRepository;
import org.ex.distributed_computing.repository.TweetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String STUB_COUNTRY = "US"; // since not supplied

    private final DiscussionCommunicationProps discussionProperties;
    private final PostRepository postRepository;
    private final TweetRepository tweetRepository;
    private final RestTemplate restTemplate;

    public List<PostResponseDTO> getAllPosts() {
        return restTemplate.exchange(
                "%s%s/posts".formatted(discussionProperties.getAddress(), discussionProperties.getBaseApiPath()),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<PostResponseDTO>>() {
                }
        ).getBody();
    }
    @Cacheable(value = "posts", key = "#id")
    public PostResponseDTO getPostById(Long id) {
        return restTemplate.getForObject(
                "%s%s/posts/%d".formatted(discussionProperties.getAddress(), discussionProperties.getBaseApiPath(), id),
                PostResponseDTO.class
        );
    }

    @CachePut(value = "posts", key = "#result.id()")
    public PostResponseDTO createPost(PostRequestDTO requestDTO) {
        tweetRepository.findById(requestDTO.tweetId())
                .orElseThrow(() -> new NotFoundException("Tweet not found"));

        Long nextPostId = postRepository.nextId();

        return restTemplate.exchange(
                "%s%s/posts".formatted(discussionProperties.getAddress(), discussionProperties.getBaseApiPath()),
                HttpMethod.POST,
                new HttpEntity<>(new PostRequestDTO(nextPostId, requestDTO.tweetId(), requestDTO.content(), STUB_COUNTRY)),
                PostResponseDTO.class
        ).getBody();
    }

    @CachePut(value = "posts", key = "#result.id()")
    public PostResponseDTO updatePost(PostRequestDTO requestDTO) {
        tweetRepository.findById(requestDTO.tweetId())
                .orElseThrow(() -> new NotFoundException("Tweet not found"));


        return restTemplate.exchange(
                "%s%s/posts".formatted(discussionProperties.getAddress(), discussionProperties.getBaseApiPath()),
                HttpMethod.PUT,
                new HttpEntity<>(new PostRequestDTO(requestDTO.id(), requestDTO.tweetId(), requestDTO.content(), STUB_COUNTRY)),
                PostResponseDTO.class
        ).getBody();
    }

    @CacheEvict(value = "posts", key = "#id")
    public void deletePost(Long id) {
        restTemplate.exchange(
                "%s%s/posts/%d".formatted(discussionProperties.getAddress(), discussionProperties.getBaseApiPath(), id),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );
    }
}

